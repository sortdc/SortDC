package org.sortdc.sortdc.database;

public class ObjectNotFoundException extends Exception {

    private Type type;

    public enum Type {

        CATEGORY, DOCUMENT, WORD
    }

    public ObjectNotFoundException(Type type) {
        this.setType(type);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null) {
            switch (this.type) {
                case CATEGORY:
                    message = "Category not found";
                    break;
                case DOCUMENT:
                    message = "Document not found";
                    break;
                case WORD:
                    message = "Word not found";
                    break;
            }
        }
        return message;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }
}
