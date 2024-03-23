import pandas as pd

# Replace 'your_file.csv' with the actual file path
file_path = 'I:/MATsim/abitDemand_20231228/legs.csv'

# Read the CSV file into a pandas DataFrame
df = pd.read_csv(file_path)

# Define the rectangular area boundaries
min_x = 4474597  # Replace with your minimum x-coordinate
max_x = 4477754  # Replace with your maximum x-coordinate
min_y = 5351616  # Replace with your minimum y-coordinate
max_y = 5354543  # Replace with your maximum y-coordinate

# Filter rows based on the rectangular area for start points
start_point_filter = (
    (df['start_x'] >= min_x) & (df['start_x'] <= max_x) &
    (df['start_y'] >= min_y) & (df['start_y'] <= max_y)
)

# Filter rows based on the rectangular area for end points
end_point_filter = (
    (df['end_x'] >= min_x) & (df['end_x'] <= max_x) &
    (df['end_y'] >= min_y) & (df['end_y'] <= max_y)
)

# Create a new DataFrame with rows that meet the conditions for start points or end points
filtered_df = df[start_point_filter | end_point_filter]

# Write the filtered DataFrame to a new CSV file
filtered_df.to_csv('I:/MATsim/abitDemand_20231228/Cropped.csv', index=False)

print(f"Filtered data has been saved")