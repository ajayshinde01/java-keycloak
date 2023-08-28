package com.keycloak.dto;
import java.util.ArrayList;
import java.util.List;

 

/**
* Provides information to UI to construct what information needs to be shown to
* the user. Right now this is still in research and may need lots of change.
* <pre>
* Few of the concerns here are mentioned below. 
* - If we add a new column to be shown for lets say product-family we will also
*   have to add JSON view. So if we to add one column we have two separate places
*   to do the change which is not ideal and error prone.
* - Even if we add column there is still pending things like lets say if we mention
*   the column type as button. Then the logic of button how it should behave on UI
*   will also need to be provided from server side. If we provide this information
*   then can it work without any UI change required?
* - If a column is added to UI then lets say we want to take inputs from user then
*   as per current code we will have to edit the form and do the mapping for it.
* </pre>
* There are issues but right now keeping it as is till there is some clarity.
*   
*/
public class DataTableMetadata {
    private List<ColumnMetadata> columnsMetadata = new ArrayList<>();

 

    /**
     * This method follows hybrid builder pattern to ease the process of
     * DataTableMetadata creation.
     * 
     * @param columnMetadata column related data. See {@link ColumnMetadata}
     * @return DataTableMetadata instance so that we can add more column metadata.
     */
    public DataTableMetadata addColumnMetadata(ColumnMetadata columnMetadata) {
        this.columnsMetadata.add(columnMetadata);
        return this;
    }

 

    public List<ColumnMetadata> getColumnsMetadata() {
        return columnsMetadata;
    }
}