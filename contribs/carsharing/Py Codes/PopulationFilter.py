import pandas as pd

# Load the Excel file
excel_file = 'C:/Users/Hp/Desktop/Run Scenario 3/Scenario3.xlsx'

# Replace 'Column_Name' with the name of the specific column you want to extract values from
column_name = 'personID'

# Read the Excel file into a DataFrame
df = pd.read_excel(excel_file)

# Extract values from the specified column
values = df[column_name].tolist()

# Remove duplicates from the list
unique_values = list(set(values))

# Print or use unique_values as needed
print(unique_values)

import pandas as pd

# Load the CSV file
csv_file = 'I:/MATsim/abitDemand_20231228/LegFiltered26Jan.csv'

# Load the list of unique values obtained from the Excel file
# unique_values = [...]  # Replace [...] with your list of unique values

# Replace 'Column_Name' with the name of the specific column you want to filter in the CSV file
column_name = 'person_id'

# Read the CSV file into a DataFrame
df = pd.read_csv(csv_file)

# Filter rows based on whether the value in 'Column_Name' exists in the list of unique values
filtered_df = df[df[column_name].isin(unique_values)]

# Save the filtered DataFrame to a new CSV file
filtered_df.to_csv('C:/Users/Hp/Desktop/Combiner/pythonProject/filtered_data.csv', index=False)