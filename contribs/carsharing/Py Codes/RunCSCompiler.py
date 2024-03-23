import os
import pandas as pd

# Path to the folder containing the text files
folder_path = 'C:/Users/Hp/Desktop/Run Scenario 3'

# List to store data frames from each text file
dfs = []

# Loop through each file in the folder
for filename in os.listdir(folder_path):
    if filename.endswith('.txt'):
        # Read the text file into a DataFrame
        df = pd.read_csv(os.path.join(folder_path, filename))
        # Append the DataFrame to the list
        dfs.append(df)

# Combine all DataFrames into a single DataFrame
combined_df = pd.concat(dfs, ignore_index=True)

# Write the combined DataFrame to an Excel file
combined_df.to_excel('combined_data.xlsx', index=False)