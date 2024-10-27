import requests
from bs4 import BeautifulSoup
import pandas as pd

# URL of website being used
url = 'https://www.fantasypros.com/nfl/reports/leaders/ppr.php?year=2023&start=1&end=18'

# Sends a request to the website
response = requests.get(url)
html_content = response.content

# Parses through the HTML content using BeautifulSoup
soup = BeautifulSoup(html_content, 'html.parser')

# Finds the table with statistics
table = soup.find('table', {'id': 'data'})

# Gets headers from table
headers = [th.text for th in table.find('thead').find_all('th')]

# Gets rows from table
rows = []
for tr in table.find('tbody').find_all('tr'):
    cells = tr.find_all('td')
    row = [cell.text.strip() for cell in cells]
    rows.append(row)

# Creates a DataFrame
df = pd.DataFrame(rows, columns=headers)

# Gets rid of weekly performances
for i in range(1, 19):
    week = str(i)
    df.pop(week)

df.pop('#')
df.pop('Team')
df.pop('TTL')

# Saves DataFrame to csv file
df.to_csv('fantasy_football_ppr_2023.csv', index=False)

print("Data scraped and saved to fantasy_football_ppr_2023.csv")