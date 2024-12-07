import json
import boto3
import urllib.request
import os
import base64  # Import base64 to encode the binary data

s3_client = boto3.client("s3")
bucket_name = os.environ["bucket"]

def lambda_handler(event, context):
    try:
        # Extract the file name from the input parameters
        zone = event['pathParameters']['zone']
        date = event['pathParameters']['date']
        object_name = f'{zone}_{date}.pdf'
        
        # Generate a pre-signed URL for the file
        file_url = s3_client.generate_presigned_url(
            'get_object',
            Params={'Bucket': bucket_name, 'Key': object_name},
            ExpiresIn=60  # The URL will be valid for 1 hour
        )
        
        # Download the file using the pre-signed URL (as binary)
        with urllib.request.urlopen(file_url) as response:
            file_data = response.read()  # Read the content as binary
        
        # Encode the binary data to base64
        file_data_base64 = base64.b64encode(file_data).decode('utf-8')  # Ensure it's a string for JSON
        
        # Set the headers to indicate that it is a downloadable file
        return {
            "statusCode": 200,
            "headers": {
                "Content-Type": "application/pdf",  # Proper MIME type for PDFs
                "Content-Disposition": f"attachment; filename={object_name}",
            },
            "body": file_data_base64,  # The body should contain the base64-encoded binary data
            "isBase64Encoded": True  # Indicate that the content is base64-encoded
        }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps(f'Error: {str(e)}')
        }
