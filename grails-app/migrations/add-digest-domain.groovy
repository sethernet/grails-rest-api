databaseChangeLog = {

    changeSet(author: "admin", id: "1467742865817-1") {
        createTable(tableName: "digest") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "digestPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "customer_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "input", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "output", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "admin", id: "1467742865817-2") {
        addForeignKeyConstraint(baseColumnNames: "customer_id", baseTableName: "digest", constraintName: "FK_qemokrpdbpcilq7grljc2w11d", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "person")
    }

}
