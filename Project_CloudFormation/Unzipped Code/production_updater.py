import json
import boto3

dynamodb = boto3.resource('dynamodb')
table_name = 'gas_production_cost'
prod_table = dynamodb.Table(table_name)

def lambda_handler(event, context):
	try:
		if not event.get('body'):
			return {
				'statusCode': 400,
				'body': json.dumps({'message': 'Empty body.'}),
				'headers': {"Content-Type": "application/json"}
			}

		req_body = json.loads(event['body'])
		
		if 'date' not in req_body or 'zone' not in req_body or 'costPerM3' not in req_body:
			return {
				'statusCode': 400,
				'body': json.dumps({'message': 'Date or cost per m3 not provided.'}),
				'headers': {"Content-Type": "application/json"}
			}
		
		req_body["id"] = req_body["zone"] + "_" + req_body["date"]
		prod_table.put_item(Item=req_body)

		return {
			'statusCode': 200,
			'body': json.dumps({'message': f'Successfully saved or updated production data for {req_body["date"]}'}),
			'headers': {"Content-Type": "application/json"}
		}
	except Exception as e:
		return {
			'statusCode': 500,
			'body': json.dumps({'message': f'Error inserting or updating item in DynamoDB: {str(e)}'}),
			'headers': {"Content-Type": "application/json"}
		}