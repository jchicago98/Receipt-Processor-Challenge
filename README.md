
# Receipt Processor

## Description

This is a Java Spring Boot application for processing receipts and calculating reward points based on specific rules.

## Features

- Calculate reward points for receipts.
- Retrieve reward points for a specific receipt.
- Dockerized for easy deployment.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)

## Installation

### Prerequisites

- Docker: Make sure you have Docker installed on your system.

### Building and Running

1. Clone this repository:

   ```sh
   git clone https://github.com/jchicago98/Receipt-Processor-Challenge.git
   cd Receipt-Processor-Challenge
   ```

2. Build the Docker image:

   ```sh
   docker build -t receipt-processor .
   ```

3. Run the Docker container:

   ```sh
   docker run -p 8080:8080 receipt-processor
   ```

The application should now be running and accessible at http://localhost:8080.

## Usage

1. Create and process receipts using the provided API endpoints (see [API Endpoints](#api-endpoints)).
2. Retrieve reward points for a specific receipt.
3. Customize the reward calculation rules in the `ReceiptService` class to match your requirements.

## API Endpoints

- `GET /receipts/{id}/points`: Retrieve reward points for a specific receipt.

  Example:

  ```sh
  curl http://localhost:8080/receipts/123/points
  ```

- `POST /receipts/process`: Process a new receipt and calculate reward points.

  Example:

  ```sh
  curl --location 'http://localhost:8080/receipts/process' 
  --header 'Content-Type: application/json' 
  --data '{
  "retailer": "M&M Corner Market",
  "purchaseDate": "2022-03-20",
  "purchaseTime": "14:33",
  "items": [
    {
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    }
  ],
  "total": "9.00"
  }'
  ```
