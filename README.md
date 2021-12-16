# README #
This is our group repository for the app "The Buzz", intended for our group members to use.

## Details
- Semester: Fall 2021
- Bitbucket Repository: https://mul223@bitbucket.org/chm321/cse216_group15.git
- App name: The Buzz
- Heroku Url: https://runtimes/heroku.com

## Contributors
1. Michelle Li
2. Anh Vu Vu
3. Jake Stocker
4. Mike Brach
5. Mary Ye (new)

## User Manuel
The Buzz is an App that is programmed for the company in order to have a secure place for professional social interaction. The app creates a platform for people to interact by giving the option to post messages, like messages and dislike messages. Users can reach out to other users and get to know their life better using The Buzz.

### Backend API Documentation

#### Phase 1: 

##### Returns all message with subject, message, number of likes and dislikes
- API: https://runtimes.herokuapp.com/messages 
- Method: GET
- Parameters: None
- Request Example: curl -s https://runtimes.herokuapp.com/messages -X GET
- Output Example: [{"mId":7,"mSubject":"hhh","mMessage":"www","mUpvotes":0,"mDownvotes":0},{"mId":8,"mSubject":"Title","mMessage":"Message","mUpvotes":0,"mDownvotes":0},{"mId":9,"mSubject":"Yeah","mMessage":"Ma","mUpvotes":0,"mDownvotes":0},{"mId":6,"mSubject":"Movie Time","mMessage":"I watched that movie saturday night","mUpvotes":1,"mDownvotes":0}]

##### Returns everything from a single row
- API: https://runtimes.herokuapp.com/messages/:id
- Method: GET
- Parameters: id of the row
- Request Example: curl -s https://runtimes.herokuapp.com/messages -X GET
- Output Example: {"mStatus":"ok","mData":{"mId":6,"mSubject":"Movie Time","mMessage":"I watched that movie saturday night","mUpvotes":1,"mDownvotes":0}

##### Add a row to the message list
- API: https://runtimes.herokuapp.com/messages
- Method: POST
- Parameteres: None
- Request JSON: {"mSubject":"Nap Time", "mMessage":"It was relaxing"}
- Output Example: {"mStatus":"ok", "mMessage":"inserted id: 1"}

##### Delete a row from all messages
- API: https://runtimes.herokuapp.com/messages/:id
- Method: DELETE
- Parameteres: id of the row
- Request JSON: https://runtimes.herokuapp.com/messages/7
- Output Example: {"mStatus":"ok", "mMessage":"deleted row 7"}

##### Edit the title and message of a row
- API: https://runtimes.herokuapp.com/messages/:id
- Method: PUT
- Parameters: id of the row
- Request JSON: {"mSubject":"Movie Time Yay", "mMessage":"I watched that movie Saturday night"}
- API example: https://runtimes.herokuapp.com/messages/6
- Output Example: {"mStatus":"ok", "mMessage":"deleted row 7"

##### Add an upvote to a message
- API: https://runtimes.herokuapp.com/messages/:id/upvote
- Method: POST
- Parameteres: id of the row
- API example: https://runtimes.herokuapp.com/messages/8
- Output Example: {"mStatus":"ok", "mMessage":"added upvote in row 8"}

##### Delete an upvote from a message
- API: https://runtimes.herokuapp.com/messages/:id/upvote
- Method: DELETE
- Parameteres: id of the row
- API example: https://runtimes.herokuapp.com/messages/6
- Output Example: {"mStatus":"ok", "mMessage":"deleted upvote in row 6"}

##### Add an downvote to a message
- API: https://runtimes.herokuapp.com/messages/:id/downvote
- Method: POST
- Parameteres: id of the row
- API example: https://runtimes.herokuapp.com/messages/7
- Output Example: {"mStatus":"ok", "mMessage":"added downvote in row 7"}

##### Delete an downvote from a message
- API: https://runtimes.herokuapp.com/messages/:id/downvote
- Method: DELETE
- Parameteres: id of the row
- API example: https://runtimes.herokuapp.com/messages/7
- Output Example: {"mStatus":"ok", "mMessage":"deleted downvote in row 7"}

#### Phase 2 new REST routes design

##### Google API endpoint for oauth
- API: https://runtimes.herokuapp.com/authenticate
- Method: POST
- Parameters: id_token
- Request JSON: "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFkZDhjMGVlNjIzOTU0NGFmNTNmOTM3MTJhNTdiMmUyNmY5NDMzNTIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiMjg1NjczNTY0NjYwLWhzZWs3YXRlNnZqMDUyYzBxOWkxYzI5MzcyYTkwZGM1LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMjg1NjczNTY0NjYwLWhzZWs3YXRlNnZqMDUyYzBxOWkxYzI5MzcyYTkwZGM1LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA5NDc0NDAzNTA5MjY3OTkzNDE1IiwiaGQiOiJsZWhpZ2guZWR1IiwiZW1haWwiOiJtdWwyMjNAbGVoaWdoLmVkdSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiaE5CU09JQTRBejNzYmRqLVVpZVU2QSIsIm5hbWUiOiJNaWNoZWxsZSBMaSIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaFNSYXNmMTR6eGVNQ2E3WXM2TDNCVWRIUWlsSThsSnhEdk5feU09czk2LWMiLCJnaXZlbl9uYW1lIjoiTWljaGVsbGUiLCJmYW1pbHlfbmFtZSI6IkxpIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2MzQ2Njc1MzQsImV4cCI6MTYzNDY3MTEzNCwianRpIjoiZDg4NDU4N2IxZDI3YjY2YjdjNmViZmY5MjRmMjBkNTFhOTc1ZWJjMiJ9.m8ssVURGOm5rlgW6ky1qRTL_yXYb8E_SpbSWU4GIRBJKzUVrJ-S2a3YGdT7IK29VqTmGLrCLnYeV9EMLX5YdfUzdCB6sZgqeeoPplJau-TSN4kTEYJtrWX3bU1G9w2mqTBZVIHPz4XfW4LEf8RJTQtTiy01c5aV-_Ep0WuKcbR9dAiQM1UVdE0tXAqnr7SQ94xDE1_d-1GECJUKuTNL7DVc7MuAlyckBtZihORrdZc28KFAelwuZyNkV0wj8N4a1vGrBnOOi_StZ14My0o2PWqHKv4jyxkvYgrD8oYs2LOh5AEGA_D2sN9_xGTse-NryT8UgntV3GYBdCbHXeAGXKg"
- Output Example: {"mStatus": "ok", "mMessage": "Session key successfully created", "mData": 1680420450}

##### Return all comments to a message
- API: https://runtimes.herokuapp.com/messages/:id/comments/:email/:sessionkey
- Method: GET
- Parameters: message id
- API example: https://runtimes.herokuapp.com/messages/2/comments
- Output Example: {
  "mStatus": "ok",
  "mData": [
    {
      "mId": 2,
      "mUserId": "mul223@lehigh.edu",
      "mMsgId": 2,
      "mBody": "First comment from web "
    },
    {
      "mId": 3,
      "mUserId": "mul223@lehigh.edu",
      "mMsgId": 2,
      "mBody": "Another comment"
    },
    {
      "mId": 5,
      "mUserId": "anv223@lehigh.edu",
      "mMsgId": 2,
      "mBody": "wwww"
    },
    {
      "mId": 6,
      "mUserId": "anv223@lehigh.edu",
      "mMsgId": 2,
      "mBody": "tttt"
    },
    {
      "mId": 7,
      "mUserId": "anv223@lehigh.edu",
      "mMsgId": 2,
      "mBody": "zzzz"
    },
    {
      "mId": 8,
      "mUserId": "mul223@lehigh.edu",
      "mMsgId": 2,
      "mBody": "yay yay yay"
    }
  ]
}

##### Add a comment to a message
- API: https://runtimes.herokuapp.com/messages/:id/comments
- Method: POST
- Parameters: message id
- Request JSON: { "mComment": "it worked!", "mUser": "mul223@lehigh.edu"}
- Output Example:{ "mStatus": "ok" }

##### Update a comment to a message
- API: https://runtimes.herokuapp.com/messages/:id/comments/:cid
- Method: PUT
- Parameters: message id, comment id
- Request JSON: { "mComment": "Update!", "mUser": "mul223@lehigh.edu"}
- Output Example: { "mStatus": "ok" }

##### Delete a comment to a message
- API: https://runtimes.herokuapp.com/messages/:id/comments/:cid
- Method: DELETE
- Parameters: message id, comment id
- Request JSON: { "mUser": "mul223@lehigh.edu"}
- Output Example:{ "mStatus": "ok" }

##### Returns all info from a user
- API: https://runtimes.herokuapp.com/users/:uid/:sessionkey
- Method: GET
- Parameters: user id, session key
- Request JSON: { "mEmail": "mul223@lehigh.edu", "mSessionKey":-1085473035 }
- Output Example: { "mStatus": "ok", "mData": {
    "mUserId": "mul223@lehigh.edu",
    "mName": "Michelle Li",
    "mBio": "Lehigh Class of 2023 yeah"
  }
}

##### Edit the bio of a user
- API: https://runtimes.herokuapp.com/users/:uid
- Method: PUT
- Parameters: user id
- Request JSON: { "mBio": "Lehigh Student"}
- Output Example: { "mStatus": "ok" }

##### Config Variables
- CLIENT_ID: 285673564660-hsek7ate6vj052c0q9i1c29372a90dc5.apps.googleusercontent.com
- CORS_ENABLED: TRUE
- DATABASE_URL: postgres://tsfckvjlfejcen:5881f854074caa7a310678a384311d9deddf589662af9198142c9708aae4619e@ec2-54-146-84-101.compute-1.amazonaws.com:5432/dapbocqmelis1n
- POSTGRES_DATABASE: dapbocqmelis1n
- POSTGRES_IP: ec2-54-146-84-101.compute-1.amazonaws.com
- POSTGRES_PASS: 5881f854074caa7a310678a384311d9deddf589662af9198142c9708aae4619e
- POSTGRES_PORT: 5432
- POSTGRES_USER: tsfckvjlfejcen

#### Phase 3 
- PM, Anh Vu
- App(flutter), Michelle Li 
- Backend, Maryyeyee
- FrontEnd-Jake
- Admin-Mike
###Devise and document a strategy for updating the database schema without deleting existing data.
- Usally when we change a field and rebuild it, the schema will update and enable the sql to drop the old and create a new column. However, if we just want to rename a column to something like age to ages, the age will be drop because the columns does not exist in the model.
- A way to prevent the renamed column from being dropped is to change the sql script that is generetad for the migration process. This can be done by using the VSchema tool that is shipped together with the product.


