DO $$
declare
  i record;
  j record;
  count integer;
  user_uuid uuid;
  doc_uuid uuid;
begin
  for i in 1..400 loop
    SELECT gen_random_uuid() INTO user_uuid;
    SELECT gen_random_uuid() INTO doc_uuid;
    INSERT INTO codexis.users(SELECT user_uuid as uuid, md5(random()::text) AS name, NOW() as timestamp);
    INSERT INTO codexis.documents(SELECT doc_uuid as uuid, user_uuid as user_uuid, NOW() as timestamp);
    SELECT floor(random() * 1200)::int INTO count;
    for j in 1..count loop
    	INSERT INTO codexis.visits(SELECT gen_random_uuid() as uuid, doc_uuid as doc_uuid, NOW() as timestamp);
    end loop;
  end loop;
end;
$$ ;