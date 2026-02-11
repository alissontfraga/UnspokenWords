CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(80) NOT NULL,
    category VARCHAR(255) NOT NULL,
    for_person VARCHAR(255) NOT NULL,
    date DATE,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_messages_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
