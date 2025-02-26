-- Drop the existing FK constraint if it exists
ALTER TABLE IF EXISTS messages DROP CONSTRAINT IF EXISTS FK8cs7qdu3mdbr08xirdmsfxpgk;
ALTER TABLE IF EXISTS messages DROP CONSTRAINT IF EXISTS FK_parent_message_id;

-- Add a unique constraint on the id column
ALTER TABLE messages ADD CONSTRAINT unique_message_id UNIQUE (id);

-- Add the FK constraint manually - use only parent_message_id as in the Supabase schema
ALTER TABLE messages 
  ADD CONSTRAINT FK_parent_message_id 
  FOREIGN KEY (parent_message_id) 
  REFERENCES messages(id) 
  ON DELETE SET NULL;