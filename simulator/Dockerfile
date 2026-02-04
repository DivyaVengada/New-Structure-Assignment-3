FROM python:3.11-slim

WORKDIR /sim

RUN pip install --no-cache-dir requests

COPY driver_simulator.py .

CMD ["python", "driver_simulator.py"]
