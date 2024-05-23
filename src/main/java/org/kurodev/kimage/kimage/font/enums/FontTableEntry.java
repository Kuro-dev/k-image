package org.kurodev.kimage.kimage.font.enums;

/**
 * Small interface to be able to implement and support more different types of tables
 *
 * @see HeadTable
 * @see HheaTable
 * @see MaxpTable
 */
public interface FontTableEntry {
    /**
     * @return the 4 byte long name of the table.
     */
    String getTable();

    /**
     * The absolute position of this entry within the table.
     * (table start position + {@code getPosition()})
     */
    int getPosition();

    /**
     * The size of this entry in bytes.
     */
    int getBytes();

    /**
     * @return Flag indicating whether this particular value is signed or unsigned
     */

    boolean isSigned();

}
