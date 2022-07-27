DROP SCHEMA IF EXISTS codexis CASCADE;

CREATE SCHEMA codexis
	CREATE TABLE users(
		uuid UUID DEFAULT gen_random_uuid(),
		name TEXT NOT NULL,
		timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
		PRIMARY KEY(uuid)
	)

	CREATE TABLE documents(
		uuid UUID DEFAULT gen_random_uuid(),
		user_uuid UUID NOT NULL,
		timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
		PRIMARY KEY(uuid),
  		CONSTRAINT fk_users
      		FOREIGN KEY(user_uuid)
	  			REFERENCES users(uuid)
	)

	CREATE TABLE visits(
		uuid UUID DEFAULT gen_random_uuid(),
		doc_uuid UUID NOT NULL,
		timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
		PRIMARY KEY(uuid),
		CONSTRAINT fk_documents
      		FOREIGN KEY(doc_uuid)
	  			REFERENCES documents(uuid)
	);

CREATE INDEX doc_uuid_index ON codexis.visits (doc_uuid);
CREATE INDEX uuid_index_v ON codexis.visits (uuid);
CREATE INDEX user_uuid_index ON codexis.documents (user_uuid);
CREATE INDEX uuid_index_d ON codexis.documents (uuid);
CREATE INDEX uuid_index_u ON codexis.users (uuid);

INSERT INTO codexis.users(uuid, name) VALUES (UUID('95d24930-303d-4c66-8d83-db349a210bdf'),'saddsadsa');
INSERT INTO codexis.documents(uuid, user_uuid) VALUES (UUID('85d24930-303d-4c66-8d83-db349a210bdf'), UUID('95d24930-303d-4c66-8d83-db349a210bdf'));
INSERT INTO codexis.documents(uuid, user_uuid) VALUES (UUID('85d24930-303d-4c66-4483-db349a210bdf'), UUID('95d24930-303d-4c66-8d83-db349a210bdf'));

