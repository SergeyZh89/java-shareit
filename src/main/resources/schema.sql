CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name  VARCHAR(255)                        NOT NULL,
    email VARCHAR(512)                        NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    description  VARCHAR(255),
    requestor_id BIGINT,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_request_user FOREIGN KEY (requestor_id) references users (id)
);
CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255),
    description  VARCHAR(255),
    is_available BOOLEAN,
    owner_id     BIGINT,
    request_id   BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_user FOREIGN KEY (owner_id) references users (id),
    CONSTRAINT fk_item_request FOREIGN KEY (request_id) references requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(255),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_item FOREIGN KEY (item_id) references items (id),
    CONSTRAINT fk_booking_user FOREIGN KEY (booker_id) references users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY,
    text_author TEXT,
    item_id     BIGINT,
    author_id   BIGINT,
    author_name VARCHAR(255),
    created     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comment_item FOREIGN KEY (item_id) references items (id),
    CONSTRAINT fk_comment_user FOREIGN KEY (author_id) references users (id)
);