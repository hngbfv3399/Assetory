-- Apply after backing up the production database.
-- The application no longer reads or writes user_roles.
DROP TABLE IF EXISTS user_roles;

-- Persist the sales model selected by a seller. ONE_TIME preserves existing behaviour.
SET @schema_name = DATABASE();

SET @sql = (
    SELECT IF(
        EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'products' AND column_name = 'sale_type'),
        'SELECT 1',
        'ALTER TABLE products ADD COLUMN sale_type VARCHAR(20) NOT NULL DEFAULT ''ONE_TIME'' AFTER price'
    )
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @sql = (
    SELECT IF(
        EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'products' AND column_name = 'minimum_price'),
        'SELECT 1',
        'ALTER TABLE products ADD COLUMN minimum_price DECIMAL(15, 2) NULL AFTER sale_type'
    )
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @sql = (
    SELECT IF(
        EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'products' AND column_name = 'release_at'),
        'SELECT 1',
        'ALTER TABLE products ADD COLUMN release_at DATETIME NULL AFTER minimum_price'
    )
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;
