DROP TABLE IF EXISTS public.user_users;

CREATE TABLE IF NOT EXISTS public.user_users(
    id VARCHAR(36) NOT NULL,
    active BOOL,
    barcode VARCHAR(19),
    created_date TIMESTAMP,
    enrollment_date TIMESTAMP,
    expiration_date TIMESTAMP,
    patron_group VARCHAR(36),
    "type" VARCHAR(6),
    updated_date TIMESTAMP,
    username VARCHAR(23),
    data JSON
);

INSERT INTO public.user_users VALUES ('00bc2807-4d5b-4a27-a2b5-b7b1ba431cc4',false,'133143370961512','2021-06-29 21:51:45.424-04','2015-04-12 20:00:00-04','2019-06-19 20:00:00-04','503a81cd-6c26-400f-b620-14c08943697c','patron','2021-06-29 21:51:45.424-04','sallie','{
    "id": "00bc2807-4d5b-4a27-a2b5-b7b1ba431cc4",
    "active": false,
    "barcode": "133143370961512",
    "createdDate": "2021-06-30T01:51:45.424+00:00",
    "departments": [],
    "enrollmentDate": "2015-04-13T00:00:00.000+00:00",
    "expirationDate": "2019-06-20T00:00:00.000+00:00",
    "metadata": {
        "createdDate": "2021-06-30T01:51:45.420+00:00",
        "updatedDate": "2021-06-30T01:51:45.420+00:00"
    },
    "patronGroup": "503a81cd-6c26-400f-b620-14c08943697c",
    "personal": {
        "addresses": [
            {
                "addressLine1": "17691 Rodriguez Divide",
                "addressTypeId": "93d3d88d-499b-45d0-9bc7-ac73c3a19880",
                "city": "Birmingham",
                "countryId": "US",
                "postalCode": "85748",
                "primaryAddress": true,
                "region": "CO"
            }
        ],
        "dateOfBirth": "2009-07-21T00:00:00.000+00:00",
        "email": "ahmad@kertzmann-bailey-and-brekke.io",
        "firstName": "Maiya",
        "lastName": "Denesik",
        "middleName": "Noel",
        "mobilePhone": "(557)093-7575",
        "phone": "759.693.8557",
        "preferredContactTypeId": "002"
    },
    "proxyFor": [],
    "type": "patron",
    "updatedDate": "2021-06-30T01:51:45.424+00:00",
    "username": "sallie"
}');

INSERT INTO public.user_users VALUES ('011dc219-6b7f-4d93-ae7f-f512ed651493',false,'897083256223023','2021-06-29 21:51:46.182-04','2018-05-05 20:00:00-04','2019-09-01 20:00:00-04','3684a786-6671-4268-8ed0-9db82ebca60b','patron','2021-06-29 21:51:46.182-04','elmer','{
    "id": "011dc219-6b7f-4d93-ae7f-f512ed651493",
    "active": false,
    "barcode": "897083256223023",
    "createdDate": "2021-06-30T01:51:46.182+00:00",
    "departments": [],
    "enrollmentDate": "2018-05-06T00:00:00.000+00:00",
    "expirationDate": "2019-09-02T00:00:00.000+00:00",
    "metadata": {
        "createdDate": "2021-06-30T01:51:46.178+00:00",
        "updatedDate": "2021-06-30T01:51:46.178+00:00"
    },
    "patronGroup": "3684a786-6671-4268-8ed0-9db82ebca60b",
    "personal": {
        "addresses": [
            {
                "addressLine1": "69175 Haley Skyway",
                "addressTypeId": "93d3d88d-499b-45d0-9bc7-ac73c3a19880",
                "city": "Marana",
                "countryId": "US",
                "postalCode": "02013-0332",
                "primaryAddress": true,
                "region": "NH"
            }
        ],
        "dateOfBirth": "1947-06-23T00:00:00.000+00:00",
        "email": "monserrat@donnelly-skiles.ge",
        "firstName": "Lois",
        "lastName": "Huels",
        "phone": "(619)645-7533 x5934",
        "preferredContactTypeId": "004"
    },
    "proxyFor": [],
    "type": "patron",
    "updatedDate": "2021-06-30T01:51:46.182+00:00",
    "username": "elmer"
}');

INSERT INTO public.user_users VALUES('01b9d72b-9aab-4efd-97a4-d03c1667bf0d',false,'908122635201927','2021-06-29 21:51:45.342-04','2017-04-26 20:00:00-04','2019-05-08 20:00:00-04','ad0bc554-d5bc-463c-85d1-5562127ae91b','patron','2021-06-29 21:51:45.342-04','rick1','{
    "id": "01b9d72b-9aab-4efd-97a4-d03c1667bf0d",
    "active": false,
    "barcode": "908122635201927",
    "createdDate": "2021-06-30T01:51:45.342+00:00",
    "departments": [],
    "enrollmentDate": "2017-04-27T00:00:00.000+00:00",
    "expirationDate": "2019-05-09T00:00:00.000+00:00",
    "metadata": {
        "createdDate": "2021-06-30T01:51:45.338+00:00",
        "updatedDate": "2021-06-30T01:51:45.338+00:00"
    },
    "patronGroup": "ad0bc554-d5bc-463c-85d1-5562127ae91b",
    "personal": {
        "addresses": [
            {
                "addressLine1": "04686 Heaney River Apt. 311",
                "addressTypeId": "1c4b225f-f669-4e9b-afcd-ebc0e273a34e",
                "city": "Laguna Woods",
                "countryId": "US",
                "postalCode": "80182",
                "primaryAddress": true,
                "region": "VA"
            }
        ],
        "dateOfBirth": "1952-11-19T00:00:00.000+00:00",
        "email": "dahlia@koch-hayes.it",
        "firstName": "Katherine",
        "lastName": "Denesik",
        "mobilePhone": "118-374-7507",
        "phone": "725-093-3336",
        "preferredContactTypeId": "002"
    },
    "proxyFor": [],
    "type": "patron",
    "updatedDate": "2021-06-30T01:51:45.342+00:00",
    "username": "rick1"
}');

