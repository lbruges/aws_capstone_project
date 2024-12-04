import json
import boto3

dynamodb = boto3.resource('dynamodb')
table_name = 'gas_production_cost'
production_table = dynamodb.Table(table_name)

def lambda_handler(event, context):
	try:
		path_params = event.get('pathParameters', {})
		target_date = path_params.get('date')
		
		if not target_date:
			return {
				'statusCode': 400,
				'body': json.dumps({'message': 'Production fetcher - date not provided.'})
			}

		result = production_table.get_item(Key={'date': target_date})
		date_production = result.get('Item')

		if not date_production:
			return {
				'statusCode': 404,
				'body': json.dumps({'message': f'Production info for date {target_date} was not found'})
			}

		return {
			'statusCode': 200,
			'body': json.dumps(date_production)
		}
	except Exception as e:
		return {
			'statusCode': 500,
			'body': json.dumps({'message':f'Error obtaining production data from DynamoDB: {str(e)}'})
		}