/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.datamodel.types.internal;

import java.util.Arrays;

import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.datamodel.api.TypedDatum;
import de.rcenvironment.core.datamodel.types.api.SmallTableTD;

/**
 * Implementation of {@link SmallTableTD}.
 * 
 * @author Christin Keutel
 * @author Sascha Zur
 * @author Jan Flink
 * @author Robert Mischke
 * @author Doreen Seider
 * @author Markus Kunde
 */
public class SmallTableTDImpl extends AbstractTypedDatum implements SmallTableTD {

    /** Maximum column count. */
    public static final int MAXIMUM_ENTRY_COUNT = 1000000;

    private final TypedDatum[][] tableEntries; // [row][column]

    public SmallTableTDImpl(TypedDatum[][] tableEntries) {
        super(DataType.SmallTable);
        int rowCount = tableEntries.length;
        int columnCount = 0;
        if (rowCount > 0) {
            columnCount = tableEntries[0].length;
        }
        if ((rowCount * columnCount) > MAXIMUM_ENTRY_COUNT) {
            throw new IllegalArgumentException("entries exceeds maximum number of entries: " + MAXIMUM_ENTRY_COUNT + ".");
        }
        this.tableEntries = tableEntries;
    }

    @Override
    public TypedDatum getTypedDatumOfCell(int rowIndex, int columnIndex) {
        return tableEntries[rowIndex][columnIndex];
    }

    @Override
    public void setTypedDatumForCell(TypedDatum typedDatum, int rowIndex, int columnIndex) {
        tableEntries[rowIndex][columnIndex] = typedDatum;
    }

    @Override
    public int getRowCount() {
        return tableEntries.length;
    }

    @Override
    public int getColumnCount() {
        int columnCount = 0;
        if (getRowCount() > 0) {
            columnCount = tableEntries[0].length;
        }
        return columnCount;
    }

    @Override
    public SmallTableTD getSubTable(int endRowIndex, int endColumnIndex) {
        return getSubTable(0, 0, endRowIndex, endColumnIndex);
    }

    @Override
    public SmallTableTD getSubTable(int beginRowIndex, int beginColumnIndex, int endRowIndex, int endColumnIndex) {
        TypedDatum[][] subTableEntries = new TypedDatum[endRowIndex - beginRowIndex][endColumnIndex - beginColumnIndex];
        for (int row = beginRowIndex; row < endRowIndex; row++) {
            for (int column = beginColumnIndex; column < endColumnIndex; column++) {
                subTableEntries[row - beginRowIndex][column - beginColumnIndex] = tableEntries[row][column];
            }
        }
        return new SmallTableTDImpl(subTableEntries);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(tableEntries);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SmallTableTD) {
            SmallTableTD other = (SmallTableTD) obj;
            TypedDatum[][] otherEntries = other.toArray();
            for (int i = 0; i < getRowCount(); i++) {
                if (!Arrays.equals(tableEntries[i], otherEntries[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public TypedDatum[][] toArray() {
        TypedDatum[][] resultArray = new TypedDatum[getRowCount()][getColumnCount()];
        for (int i = 0; i < getRowCount(); i++) {
            System.arraycopy(tableEntries[i], 0, resultArray[i], 0, getColumnCount());
        }
        return resultArray;
    }
    
    @Override
    public String toLengthLimitedString(int maxLength) {
        String text = "[";
        String formattedLabel = "";

        TypedDatum[][] tdArray = toArray();
        for (int j = 0; j < getColumnCount(); j++) {
            TypedDatum datum = tdArray[0][j];
            text += datum.toString();
            text += ",";
        }
        // remove last comma and whitespace
        text = text.substring(0, text.length() - 1);
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength - 4) + "...";
        }
        text += "]";
        text += "...";
        formattedLabel += text;
        String dimensionsText = " (" + "size: " + getRowCount() + "x" + getColumnCount() + ")";
        formattedLabel += dimensionsText;
        
        return formattedLabel;
    }
    @Override
    public String toString() {
        String fullContent = "";
        // fill fullcontent String
        TypedDatum[][] tdArray = toArray();
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                fullContent += tdArray[i][j];
                fullContent += ", ";
            }
            fullContent = fullContent.substring(0, fullContent.length() - 2);
            fullContent += "\r\n";
        }
        return fullContent;
    }
}
