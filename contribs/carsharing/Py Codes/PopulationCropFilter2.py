import pandas as pd

# Replace 'your_file.csv' with the actual file path
file_path = 'I:/MATsim/abitDemand_20231228/CroppedAgain.csv'

# Read the CSV file into a pandas DataFrame
df = pd.read_csv(file_path)

# Filter rows based on the value of a specific column

filtered_df = df[df['start_time_min']>=0]

# Write the filtered DataFrame to a new CSV file
filtered_df.to_csv('I:/MATsim/abitDemand_20231228/Cropped2.csv', index=False)