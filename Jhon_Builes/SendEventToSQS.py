import boto3
import os
import json
from datetime import datetime, timedelta, timezone

# Inicializar el cliente SQS
sqs = boto3.client('sqs')

# Obtener la URL de la cola SQS desde una variable de entorno
queue_url = os.environ['SQS_QUEUE_URL']

def execution_event_bridge():
    utc_menos_5 = timezone(timedelta(hours=-5))
    fecha_utc_menos_5 = datetime.now(utc_menos_5)
    today = fecha_utc_menos_5.strftime("%m-%Y")
    body_json = {"zone": "A",
                        "date": today}
    message_body_json = json.dumps(body_json)

    response = sqs.send_message(
        QueueUrl=queue_url,
        MessageBody=message_body_json,
        MessageAttributes={
            'EventType': {
                'StringValue': 'CustomEvent',  
                'DataType': 'String'
            }
        }
    )

    print(f"Mensaje enviado con éxito a SQS mediante event bridge. ID del mensaje: {response['MessageId']}")

def lambda_handler(event, context):
    """
    Función Lambda para enviar eventos recibidos a una cola SQS.
    """
    try:
       
        data = event["SQSEvent"]

        if not data:
            data = json.dumps(event.get("body")["SQSEvent"])

        for record in data:
            if record["zone"] == "":
                execution_event_bridge()
                return 
    
            body_json = {"zone": record["zone"],
                        "date": record["date"]}
            message_body_json = json.dumps(body_json)

            response = sqs.send_message(
                QueueUrl=queue_url,
                MessageBody=message_body_json,
                MessageAttributes={
                    'EventType': {
                        'StringValue': 'CustomEvent',  
                        'DataType': 'String'
                    }
                }
            )

            print(f"Mensaje enviado con éxito a SQS. ID del mensaje: {response['MessageId']}")

        return {
            "statusCode": 200,
            "body": json.dumps({"message": "Eventos enviados a SQS con éxito"})
        }

    except Exception as e:
        print(f"Error enviando mensajes a SQS: {str(e)}")
        return {
            "statusCode": 500,
            "body": json.dumps({"error": str(e)})
        }