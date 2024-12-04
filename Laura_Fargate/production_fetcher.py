import json
import boto3
from decimal import Decimal

dynamodb = boto3.resource('dynamodb')
table_name = 'gas_production_cost'
production_table = dynamodb.Table(table_name)

def decimal_to_float(obj):
	if isinstance(obj, Decimal):
		return float(obj)
	elif isinstance(obj, dict):
		return {k: decimal_to_float(v) for k, v in obj.items()}
	elif isinstance(obj, list):
		return [decimal_to_float(i) for i in obj]
	else:
		return obj

def lambda_handler(event, context):
	try:
		path_params = event.get('pathParameters', {})
		target_date = path_params.get('date')
		target_zone = path_params.get('zone')
		
		if not target_date or not target_zone:
			return {
				'statusCode': 400,
				'body': json.dumps({'message': 'Production fetcher - date not provided.'})
			}

		id_to_query = target_zone + "_" + target_date

		result = production_table.get_item(Key={'id': id_to_query, 'date': target_date})
		date_production = result.get('Item')

		if not date_production:
			return {
				'statusCode': 404,
				'body': json.dumps({'message': f'Production info for date {target_date} was not found'})
			}

		date_production = decimal_to_float(date_production)

		return {
			'statusCode': 200,
			'body': json.dumps(date_production)
		}
	except Exception as e:
		return {
			'statusCode': 500,
			'body': json.dumps({'message':f'Error obtaining production data from DynamoDB: {str(e)}'})
		}