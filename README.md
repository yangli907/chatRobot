## Overview

This is the scoring parity runner, which kicks off timeline nearby search request to target server, and output the server response in human readable format. It further integrates with result analysis process for parity comparison (in dev).

## How to run parity test

```
./scoring_request_runner.py \
   -s <server> \
   -t <test_case_file> \
   -o <output_file> \
   -u <username> \
   -p <password>")

e.g. ./scoring_request_runner.py -s mobtst02d.d.tripadvisor.com -t test_case.yaml -u yangli@tripadvisor -p mypassword
```

## Sample output file

The following snippet is the scoring parity test response returned from one server. Note this test is aimed to run against two servers running new and old algorithms, and compare the result. (The comparison and result analysis script is currently in development.)

```
TEST#1
request: {'latitude': 37.448988, 'longitude': -122.160879, 'start_date': '2016-07-24T11:58-0700', 'end_date': '2016-07-24T12:58-0700', 'accuracy': 5}
guessed_location: 2374039

nearby\_locations:
rank#1:
location_id=2374039
score=0.9979414523213952
confidence=HIGH
confidence_percentage=96
scoring_factors={'DISTANCE_FACTOR': '0.6422918', 'NO_REVIEW_FLAG': '0.0', 'LOG_USER_HISTORY_WEIGHTED_SCORE': '0.92549646', 'SCORE_MODEL_P': '0.99794143', 'USER_HISTORY_WEIGHTED_SCORE': '2.5231206', 'USER_HISTORY_WEIGHTED_SCORE_PERCENTAGE': '0.9894813', 'IS_PREVIOUS_VALIDATED': '1.0', 'NEARBY_CONFIRMATION_MEAN': '1.0', 'P_HOUR_DAYOFWEEK_D_OTHER_PRIVATE': '0.0', 'P_HOUR_DAYOFWEEK_D_10022': '0.008988', 'P_HOUR_DAYOFWEEK_D_10023': '0.0', 'P_HOUR_DAYOFWEEK_D_10021': '0.0', 'DOW_HOD_HISTOGRAM_FACTOR': '0.008988', 'WITH_TOP_RANKED_VALIDATIONS': '0.0', 'RECENT_VALIDATIONS_PERCENTAGE': '0.0', 'LOG_TOTAL_REVIEW': '3.4339871', 'CATEGORY_FACTOR_OTHER_PRIVATE': '0.0', 'CATEGORY_FACTOR_10021': '0.0', 'CATEGORY_FACTOR_10023': '0.0', 'CATEGORY_FACTOR_10022': '1.0', 'INTERCEPT_TERM': '1.0', 'UHW_SCORE_PREVIOUS_VISITS': '0.9894813', 'P_HOUR_DAYOFWEEK_D_10038': '0.0', 'CATEGORY_FACTOR_10038': '0.0'}
```

## Author

yangli
