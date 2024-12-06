import random
import os
import json
import mysql.connector
from mysql.connector import Error

def handle_db_fetching(zone, target_month, target_year):
    host = os.getenv('DB_HOST')
    user = os.getenv('DB_USER')
    password = os.getenv('DB_PWD')
    database = 'gas_consumption'

    if not host or not user or not password or not database:
        return {
            'statusCode': 500,
            'body': json.dumps({'message': 'Missing database environment variables'}),
            'headers': {"Content-Type": "application/json"}
        }
    
    connection = None
    cursor = None

    try:
        connection = mysql.connector.connect(
            host = host,
            user = user,
            password = password,
            database = database
        )
        
        date_zone_query = f'SELECT month, year, truncate(avg(consumption), 2) as avg_consumption, zone FROM gas_consumption WHERE month = {target_month}  AND  year = {target_year} AND zone = "{zone}" GROUP BY month, year, zone'

        cursor = connection.cursor(dictionary=True)
        cursor.execute(date_zone_query)

        row = cursor.fetchone()

        if not row:
            return {
                'statusCode': 404,
                'body': json.dumps({'message': 'No results found'}),
                'headers': {"Content-Type": "application/json"}
            }
        
        result = {}
        result['month'] = row['month'] 
        result['year'] = row['year']
        result['avgConsumption'] = row['avg_consumption'] 
        result['zone'] = row['zone'] 

        return {
            'statusCode': 200,
            'body': json.dumps(result),
            'headers': {"Content-Type": "application/json"}
        }
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({'message': f'Error connecting to the DB {str(e)}'}),
            'headers': {"Content-Type": "application/json"}
        }
    finally:
        if connection and connection.is_connected():
            if cursor:
                cursor.close()
            connection.close()

def lambda_handler(event, context):
    path_params = event.get('pathParameters', {})
    zone = path_params['zone']
    date = path_params['date']

    if not zone or not date:
        return {
            'statusCode': 400,
            'body': json.dumps({'message': 'Zone or date not provided'}),
            'headers': {"Content-Type": "application/json"}
        }

    date_split = date.split('-')
    if len(date_split) != 2:
        return {
            'statusCode': 400,
            'body': json.dumps({'message': 'Incorrect date format'}),
            'headers': {"Content-Type": "application/json"}
        }

    target_month = date_split[0]
    target_year = date_split[1]

    db_enabled = os.getenv('DB_ENABLED')
    if db_enabled and db_enabled == 'true':
        return handle_db_fetching(zone, target_month, target_year)

    result = {
        'month': target_month,
        'year': target_year,
        'avgConsumption': random.randint(200, 350),
        'zone': zone
    }

    return {
        'statusCode': 200,
        'body': json.dumps(result),
        'headers': {"Content-Type": "application/json"}
    }