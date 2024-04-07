import pandas as pd
import numpy as np
from sqlalchemy import create_engine
from flask import Flask, request, jsonify
import joblib

# Database connection parameters
username = 'postgres'
password = 'adminpassword'
host = 'localhost'
port = '5430'  # Ensure this is the correct port for your PostgreSQL instance
database_name = 'renilalgo'
table_name = 'merged_df'



def fill_nan_values(df):
    """
    This function fills NaN values in each column of the given DataFrame
    with the mean of that column. If the column is entirely NaN, it fills with 0.

    Parameters:
    df (pandas.DataFrame): The DataFrame in which NaN values will be filled.
    """
    for column in df.columns:
        # Check if the column has NaN values
        if df[column].isna().sum() > 0:
            mean_value = df[column].mean()
            # If the mean is NaN (column is entirely NaN), set to 0 or another appropriate value
            if pd.isna(mean_value):
                df[column].fillna(0, inplace=True)
            else:
                df[column].fillna(mean_value, inplace=True)

    return df

def connect_to_database(username, password, host, port, database_name):
    print(f"Connecting to the PostgreSQL database at {host}:{port}...")
    connection_string = f"postgresql://{username}:{password}@{host}:{port}/{database_name}"
    engine = create_engine(connection_string)
    print("Connected to the database successfully.")
    return engine

# Establish the database connection and fetch data
def fetch_data(engine, table_name):
    with engine.connect() as connection:
        query = f"SELECT * FROM public.{table_name};"
        df = pd.read_sql_query(query, connection)
    return df

engine = connect_to_database(username, password, host, port, database_name)
merged_df = fetch_data(engine, table_name)

# Now df contains the data from your database table 'merged_df'

# Sort the dataframe by 'timestamps' column in ascending order (oldest to newest)
merged_df = merged_df.sort_values(by=['instrument_token', 'timestamps'])

# Calculate the percentage change in 'close_price' for each 'instrument_token'
# This will compare each day's close price with the previous day's close price for the same instrument_token
# Using transform to ensure alignment and adding a small number (epsilon) to avoid division by zero
epsilon = 1e-8
merged_df['%changedaily'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: (x.diff() / (x.shift() + epsilon)) * 100)

# Define a small number to prevent division by zero
epsilon = 1e-8

# Function to calculate percentage change with epsilon to avoid division by zero
def safe_pct_change(series, periods):
    return (series.diff(periods) / (series.shift(periods) + epsilon)) * 100

# Calculate weekly percentage change
merged_df['%change_weekly'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: safe_pct_change(x, periods=5))

# Calculate monthly percentage change
merged_df['%change_monthly'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: safe_pct_change(x, periods=21))

# Calculate quarterly percentage change
merged_df['%change_quarterly'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: safe_pct_change(x, periods=63))

# Calculate yearly percentage change
merged_df['%change_yearly'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: safe_pct_change(x, periods=252))

# Ensure 'timestamps' column is in datetime format
merged_df['timestamps'] = pd.to_datetime(merged_df['timestamps'])

# Sort the DataFrame by 'instrument_token' and 'timestamps' to ensure chronological order for each instrument
merged_df = merged_df.sort_values(by=['instrument_token', 'timestamps'])

# Calculate EMA for the specified periods for each 'instrument_token'
ema_periods = [5, 75, 200]
for period in ema_periods:
    column_name = f'EMA_{period}'
    merged_df[column_name] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: x.ewm(span=period, adjust=True).mean())

def calculate_rsi(series, period=14):
    # Calculate daily price changes
    delta = series.diff(1)

    # Separate gains and losses
    gain = delta.clip(lower=0)
    loss = -delta.clip(upper=0)

    # Calculate the average of gains and losses
    avg_gain = gain.rolling(window=period).mean()
    avg_loss = loss.rolling(window=period).mean()

    # Calculate the relative strength (RS)
    RS = avg_gain / avg_loss

    # Calculate the RSI
    RSI = 100 - (100 / (1 + RS))
    return RSI

# Ensure the 'timestamps' column is in datetime format and sort the DataFrame
merged_df['timestamps'] = pd.to_datetime(merged_df['timestamps'])
merged_df = merged_df.sort_values(by=['instrument_token', 'timestamps'])

# Calculate RSI for specified periods and add to the DataFrame
merged_df['RSI_9'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: calculate_rsi(x, 9))
merged_df['RSI_14'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: calculate_rsi(x, 14))
merged_df['RSI_21'] = merged_df.groupby('instrument_token')['close_price'].transform(lambda x: calculate_rsi(x, 21))

# Ensure the 'timestamps' column is in datetime format and sort the DataFrame
merged_df['timestamps'] = pd.to_datetime(merged_df['timestamps'])
merged_df = merged_df.sort_values(by=['instrument_token', 'timestamps'])

# Directly calculate the MACD for each 'instrument_token' by subtracting the 26-period EMA from the 12-period EMA
merged_df['MACD'] = merged_df.groupby('instrument_token')['close_price'].transform(
    lambda x: x.ewm(span=12, adjust=False).mean()) - merged_df.groupby('instrument_token')['close_price'].transform(
    lambda x: x.ewm(span=26, adjust=False).mean())

# Calculate the signal line for each 'instrument_token' as the 9-period EMA of the MACD
merged_df['MACD_signal'] = merged_df.groupby('instrument_token')['MACD'].transform(
    lambda x: x.ewm(span=9, adjust=False).mean())

# Display the last few rows to confirm the new columns
# Ensure the 'timestamps' column is in datetime format and sort the DataFrame
merged_df['timestamps'] = pd.to_datetime(merged_df['timestamps'])
merged_df = merged_df.sort_values(by=['instrument_token', 'timestamps'])

# Directly calculate the percentage change of the current close_price with the VWAP on a rolling 5-day basis
merged_df['%change_close_VWAP'] = merged_df.groupby('instrument_token').apply(
    lambda x: ((x['close_price'] - (x['close_price'] * x['volume']).rolling(window=5).sum() / x['volume'].rolling(window=5).sum())
               / ((x['close_price'] * x['volume']).rolling(window=5).sum() / x['volume'].rolling(window=5).sum())) * 100
).reset_index(level=0, drop=True)



# Assuming merged_df is already defined and includes 'timestamps', 'instrument_token', 'close_price', and 'volume'

# Preprocessing to ensure the data is sorted properly
merged_df = merged_df.sort_values(by=['instrument_token', 'timestamps'])

# Define a function to calculate percentage changes from VWAP without storing VWAP values in the DataFrame
def calculate_percentage_changes(group):
    # Calculate VWAP within the group but don't store in the DataFrame
    VWAP_weekly = (group['close_price'] * group['volume']).rolling(window=5).sum() / group['volume'].rolling(window=5).sum()
    VWAP_monthly = (group['close_price'] * group['volume']).rolling(window=21).sum() / group['volume'].rolling(window=21).sum()
    VWAP_yearly = (group['close_price'] * group['volume']).rolling(window=252).sum() / group['volume'].rolling(window=252).sum()

    # Calculate and store percentage changes directly in the group DataFrame
    group['%change_close_VWAP_monthly'] = ((group['close_price'] - VWAP_monthly) / VWAP_monthly) * 100
    group['%change_close_VWAP_yearly'] = ((group['close_price'] - VWAP_yearly) / VWAP_yearly) * 100

    # Replace potential infinite values with NaN in percentage changes
    group['%change_close_VWAP_monthly'].replace([np.inf, -np.inf], np.nan, inplace=True)
    group['%change_close_VWAP_yearly'].replace([np.inf, -np.inf], np.nan, inplace=True)

    return group

# Apply the function to each group to calculate and add percentage changes
merged_df = merged_df.groupby('instrument_token').apply(calculate_percentage_changes)
 
# Function to calculate CCI (without storing mean deviation separately)
def calculate_indicators(df, periods):
    # Pre-calculate Typical Price and store it to avoid recalculating
    TP = (df['high_price'] + df['low_price'] + df['close_price']) / 3
    df['TP'] = TP

    for period in periods:
        # Mean deviation calculation only for use in CCI formula
        SMA_TP = TP.rolling(window=period).mean()
        mean_deviation = (TP - SMA_TP).abs().rolling(window=period).mean()

        # CCI calculation using the mean_deviation
        CCI = (TP - SMA_TP) / (0.015 * mean_deviation)
        df[f'CCI_{period}'] = CCI

    # Drop the 'TP' column as it's no longer needed after calculations
    df.drop('TP', axis=1, inplace=True)

    return df

# Assuming 'merged_df' is a pre-existing pandas DataFrame with necessary columns
# Pre-process the DataFrame
merged_df = merged_df.reset_index(drop=True)
merged_df = merged_df.set_index('instrument_token')
merged_df = merged_df.sort_index(level=[0, 1])  # Sort by 'instrument_token' and then timestamps

# Define periods for calculation
periods = [9, 14, 21]

# Calculate CCI in one go to avoid extra memory usage
merged_df = merged_df.groupby('instrument_token', group_keys=False).apply(calculate_indicators, periods)

# Resetting the index after processing
merged_df = merged_df.reset_index(drop=False)

# Ensure the 'timestamps' column is in datetime format
merged_df['timestamps'] = pd.to_datetime(merged_df['timestamps'])

# If the DataFrame has a multi-level index, handle it properly
if 'instrument_token' in merged_df.index.names or 'level_1' in merged_df.index.names:
    # Rename the index levels to avoid conflicts when they are reset to columns
    merged_df = merged_df.rename_axis(index={name: f'index_{name}' for name in merged_df.index.names})

# Now safely reset the index
merged_df = merged_df.reset_index()

# Ensure no duplicate columns after reset
merged_df = merged_df.loc[:, ~merged_df.columns.duplicated()]

# Sort the DataFrame by 'instrument_token' and 'timestamps'
merged_df.sort_values(by=['instrument_token', 'timestamps'], inplace=True)

# Define a small number to prevent division by zero
epsilon = 1e-8

# Calculate the percentage difference from the 52-week high and low for each 'instrument_token'
merged_df['%diff_52_week_high'] = merged_df.groupby('instrument_token')['close_price'].transform(
    lambda x: (x - x.rolling(window=252).max()) / (x.rolling(window=252).max() + epsilon) * 100
)
merged_df['%diff_from_52_week_low'] = merged_df.groupby('instrument_token')['close_price'].transform(
    lambda x: (x - x.rolling(window=252).min()) / (x.rolling(window=252).min() + epsilon) * 100
)

def calculate_pvroc_and_volume_roc(df, period=14):
    # Define a small number to prevent division by zero
    epsilon = 1e-8

    # Calculate the Rate of Change for the price, adding epsilon to prevent division by zero
    price_roc = df['close_price'].pct_change(periods=period).replace([np.inf, -np.inf], np.nan) * 100

    # Calculate the Rate of Change for the volume, adding epsilon to prevent division by zero
    volume_roc = df['volume'].pct_change(periods=period).replace([np.inf, -np.inf], np.nan) * 100

    df[f'Volume_ROC_{period}'] = volume_roc  # Store the volume rate of change
    df[f'PVROC_{period}'] = price_roc * volume_roc  # Combine the two to get a Price-Volume Rate of Change

    return df


# Apply the PVROC and Volume ROC function to the DataFrame
periods = [7, 14, 28, 56, 252]  # You can choose periods that make sense for your analysis
for period in periods:
    merged_df = calculate_pvroc_and_volume_roc(merged_df, period)
 
def calculate_obv(df):
    # Calculate the daily price change
    price_change = df['close_price'].diff()

    # Determine the direction of trade for each day
    direction = price_change.apply(np.sign)

    # Calculate OBV by cumulatively summing the volume adjusted by its trading direction
    obv = (direction * df['volume']).cumsum()

    return obv

# Apply the OBV function to the DataFrame, grouped by 'instrument_token'
merged_df['OBV'] = merged_df.groupby('instrument_token').apply(calculate_obv).reset_index(level=0, drop=True)

def calculate_volume_rsi(df, period=14):
    # Calculate daily volume change
    delta_volume = df['volume'].diff()

    # Separate the up-volume and down-volume
    up_volume = delta_volume.clip(lower=0)
    down_volume = -delta_volume.clip(upper=0)

    # Calculate the exponential moving average of up-volume and down-volume
    avg_up_volume = up_volume.rolling(window=period).mean()
    avg_down_volume = down_volume.rolling(window=period).mean()

    # Calculate the Volume RSI
    RS = avg_up_volume / avg_down_volume
    volume_rsi = 100 - (100 / (1 + RS))

    return volume_rsi

# Apply the Volume RSI function to each group in the DataFrame
merged_df['Volume_RSI_14'] = merged_df.groupby('instrument_token').apply(lambda x: calculate_volume_rsi(x, 14)).reset_index(level=0, drop=True)
 
def calculate_vpt(df):
    # Calculate percentage change in price
    price_change = df['close_price'].pct_change()

    # Calculate the VPT
    vpt = (df['volume'] * price_change).cumsum()

    return vpt

# Apply the VPT function to each group in the DataFrame
merged_df['VPT'] = merged_df.groupby('instrument_token').apply(lambda x: calculate_vpt(x)).reset_index(level=0, drop=True)

def calculate_mfi(group, period=14):
    # Calculate Typical Price
    typical_price = (group['high_price'] + group['low_price'] + group['close_price']) / 3

    # Calculate Raw Money Flow
    raw_money_flow = typical_price * group['volume']

    # Shift the typical price to compare with the previous day
    shifted_typical_price = typical_price.shift(1)

    # Calculate Positive and Negative Money Flow
    positive_flow = pd.Series(np.where(typical_price > shifted_typical_price, raw_money_flow, 0), index=group.index)
    negative_flow = pd.Series(np.where(typical_price < shifted_typical_price, raw_money_flow, 0), index=group.index)

    # Calculate the 14-period sum of Positive and Negative Money Flow
    positive_flow_sum = positive_flow.rolling(window=period).sum()
    negative_flow_sum = negative_flow.rolling(window=period).sum()

    # Calculate Money Flow Ratio
    money_flow_ratio = positive_flow_sum / negative_flow_sum

    # Calculate Money Flow Index
    mfi = 100 - (100 / (1 + money_flow_ratio))

    return mfi

# Apply the MFI function to each group in the DataFrame and compute MFI
merged_df['MFI_14'] = merged_df.groupby('instrument_token', group_keys=False).apply(calculate_mfi)



def calculate_cmo(df, period=10):
    # Calculate price change from previous day
    delta = df['close_price'].diff()

    # Sum of gains and losses
    gain = delta.where(delta > 0, 0).rolling(window=period).sum()
    loss = -delta.where(delta < 0, 0).rolling(window=period).sum()

    # Calculate the Chande Momentum Oscillator
    cmo = ((gain - loss) / (gain + loss)) * 100

    return cmo

# Apply the CMO function to each group in the DataFrame
merged_df['CMO_10'] = merged_df.groupby('instrument_token').apply(lambda x: calculate_cmo(x)).reset_index(level=0, drop=True)

merged_df = fill_nan_values(merged_df)


# Define the list of indicators to be used for momentum score calculation
# List of all columns for which you want to calculate the correlation
indicators = [
    'EMA_5', 'EMA_75', 'EMA_200',
              'RSI_9', 'RSI_14', 'RSI_21',
    'CCI_9', 'CCI_14', 'CCI_21', 'MACD', 'MACD_signal',
           '%change_close_VWAP_monthly', '%change_close_VWAP_yearly', '%change_close_VWAP',
              '%diff_52_week_high', '%diff_from_52_week_low',
    'Volume_ROC_7', 'PVROC_7', 'Volume_ROC_14', 'PVROC_14', 'Volume_ROC_28', 'PVROC_28',
    'Volume_ROC_56', 'PVROC_56', 'Volume_ROC_252', 'PVROC_252', 'OBV', 'Volume_RSI_14',
    'CMO_10', 'volume',
    'INDIA VIX', 'NIFTY 50', 'NIFTY 500',
       'NIFTY AUTO', 'NIFTY BANK',
       'NIFTY COMMODITIES', 'NIFTY CONSR DURBL', 'NIFTY CONSUMPTION',
       'NIFTY CPSE', 'NIFTY ENERGY',
       'NIFTY FMCG',
       'NIFTY HEALTHCARE', 'NIFTY INDIA MFG',
        'NIFTY IT',
       'NIFTY MEDIA', 'NIFTY METAL', 'NIFTY MICROCAP250',
       'NIFTY PHARMA', 'NIFTY PSE', 'NIFTY PSU BANK', 'NIFTY PVT BANK',
       'NIFTY REALTY',  'NIFTY SMLCAP 100']

# Convert indicator columns to numeric, handling errors and ensuring no data loss
for column in indicators:
    merged_df[column] = pd.to_numeric(merged_df[column], errors='coerce')

# Function to normalize indicators and calculate momentum score
def normalize_and_calculate_momentum(group):
    normalized = (group[indicators] - group[indicators].min()) / (group[indicators].max() - group[indicators].min())
    return normalized.mean(axis=1)

# Apply the function and calculate the momentum score for each instrument_token
momentum_scores = merged_df.groupby('instrument_token').apply(normalize_and_calculate_momentum)

# Ensure that the resulting series aligns with the DataFrame's index
merged_df['momentum_score'] = momentum_scores.values  # Assign the values directly to match the DataFrame's structure

# Display the last few rows to confirm the new column
print(merged_df[['timestamps', 'instrument_token', 'close_price', 'momentum_score']].tail())

# Assuming merged_df is already defined and contains the columns 'instrument_token', 'timestamps', and 'close_price'

# Sort the DataFrame
merged_df.sort_values(by=['instrument_token', 'timestamps'], inplace=True)

# Define a small number to prevent division by zero
epsilon = 1e-8

# Calculate the return over the next 22 days and 10 days
# Shift the close price 22 days back and 10 days back and then calculate the return
merged_df['future_return_10'] = (merged_df.groupby('instrument_token')['close_price'].shift(-10) / (merged_df['close_price'] + epsilon)) - 1
merged_df['future_return_22'] = (merged_df.groupby('instrument_token')['close_price'].shift(-22) / (merged_df['close_price'] + epsilon)) - 1

# Create a column where 1 indicates a return greater than 10% in the next 22 days, and 0 otherwise
merged_df['above_10_percent_return_next_10_days'] = (merged_df['future_return_10'] > 0.10).astype(int)
merged_df['above_10_percent_return_next_22_days'] = (merged_df['future_return_22'] > 0.10).astype(int)

# Showing the modified DataFrame
print(merged_df[['instrument_token', 'timestamps', 'close_price', 'above_10_percent_return_next_10_days', 'above_10_percent_return_next_22_days']].tail())


# Find the latest record for each instrument_token
latest_records = merged_df.loc[merged_df.groupby('instrument_token')['timestamps'].idxmax()]

# Connect to the database
with engine.connect() as connection:
    # Assuming you want to replace the existing table or append to it
    # If you want to replace: if_exists='replace'
    # If you want to append: if_exists='append'
    latest_records.to_sql(table_name, con=connection, if_exists='append', index=False)

app = Flask(__name__)
model = joblib.load('../models/sgd_classifier_model.joblib')

@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json(force=True)  
    prediction = model.predict(latest_records)
    return jsonify({'prediction': prediction.tolist()})
