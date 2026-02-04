import requests
import time
import os

BASE_URL = os.environ.get('BASE_URL', 'http://localhost:8080/api')

def purchase(amount: float, nonce: str):
    print("\n=== Purchase ===")
    resp = requests.post(f"{BASE_URL}/payment/purchase",
                         json={"amount": amount, "paymentMethodNonce": nonce})
    print("Response:", resp.text)
    return resp.json()

def authorize(amount: float, nonce: str):
    print("\n=== Authorize ===")
    resp = requests.post(f"{BASE_URL}/payment/authorize",
                         json={"amount": amount, "paymentMethodNonce": nonce})
    print("Response:", resp.text)
    return resp.json()

def capture(order_id: str, amount: float):
    print("\n=== Capture ===")
    resp = requests.post(f"{BASE_URL}/payment/capture",
                         json={"orderId": order_id, "amount": amount})
    print("Response:", resp.text)
    return resp.json()

def cancel(order_id: str):
    print("\n=== Cancel ===")
    resp = requests.post(f"{BASE_URL}/payment/cancel",
                         json={"orderId": order_id})
    print("Response:", resp.text)
    return resp.json()

def refund(order_id: str, amount: float):
    print("\n=== Refund ===")
    resp = requests.post(f"{BASE_URL}/payment/refund",
                         json={"orderId": order_id, "amount": amount})
    print("Response:", resp.text)
    return resp.json()

def create_subscription(amount: float):
    print("\n=== Create Subscription ===")
    resp = requests.post(f"{BASE_URL}/subscription/create",
                         json={"monthlyAmount": amount})
    print("Response:", resp.text)
    return resp.json()

def send_webhook(event_id: str, event_type: str, payload: str):
    print(f"\n=== Webhook Event: {event_type} ===")
    resp = requests.post(f"{BASE_URL}/webhook/event",
                         json={"eventId": event_id,
                               "eventType": event_type,
                               "payload": payload})
    print("Response:", resp.text)
    return resp.text

def main():
    print("Waiting for app to start...")
    time.sleep(10)

    # Payment flow
    purchase_resp = purchase(49.99, "fake-nonce-123")
    order_id = purchase_resp.get("orderId", "ORDER123")

    authorize_resp = authorize(29.99, "fake-nonce-456")
    auth_order_id = authorize_resp.get("orderId", "ORDER456")

    capture(auth_order_id, 29.99)
    cancel(order_id)
    refund(order_id, 10.00)

    # Subscription flow
    sub_resp = create_subscription(19.99)
    sub_id = sub_resp.get("subscriptionId", "SUB123")

    # Webhook flow
    send_webhook("EVT001",
                 "net.authorize.customer.subscription.suspended",
                 f'{{"payload": {{"customerSubscriptionId": "{sub_id}"}}}}')
    send_webhook("EVT002",
                 "net.authorize.customer.subscription.reactivated",
                 f'{{"payload": {{"customerSubscriptionId": "{sub_id}"}}}}')
    send_webhook("EVT003",
                 "net.authorize.customer.subscription.cancelled",
                 f'{{"payload": {{"customerSubscriptionId": "{sub_id}"}}}}')

    print("\nSimulation complete.")

if __name__ == "__main__":
    main()