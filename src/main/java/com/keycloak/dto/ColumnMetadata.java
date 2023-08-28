package com.keycloak.dto;
public class ColumnMetadata {
    private String name;
    private String mappedBy;
    private String sortBy;
    private ColumnType type;
    private int width;

 

    /**
     * Construct an instance of ColumnMetadata
     * 
     * @param name     name of the column to be displayed on UI
     * @param mappedBy what field or attribute it should look at from the response
     *                 to map that column's value
     * @param type     type of the column. See {@link ColumnType}
     * @param width    width of the column in percentage without the % sign
     */
    public ColumnMetadata(String name, String mappedBy,String sortBy, ColumnType type, int width) {
        this.name = name;
        this.mappedBy = mappedBy;
        this.sortBy=sortBy;
        this.type = type;
        this.width = width;
    }

 

    public String getName() {
        return name;
    }

 

    public String getMappedBy() {
        return mappedBy;
    }

 

    public ColumnType getType() {
        return type;
    }

 

    public int getWidth() {
        return width;
    }

 

    public String getSortBy() {
        return sortBy;
    }
}