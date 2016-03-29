package bronz.utilities.files.parser;

import java.util.List;
import java.util.Map;

public interface FileParser<T,U>
{
    String FILE_PATH_PROP_NAME = "bronz.util.parser.filepath";
    String EXCEL_SHEETNAME_PROP_NAME = "bronz.util.parser.excelsheetname";
    String EXCEL_COL_TO_FIELD_MAPPINGS_PROP_NAME =
            "bronz.util.parser.excelcoltofieldmappings";
    String VALUE_CLASSNAME_PROP_NAME = "bronz.util.parser.classname";
    String KEY_FIELD_PROP_NAME = "bronz.util.parser.keyfieldname";
    String FIELD_NAMES_PROP_NAME = "bronz.util.parser.fieldnames";
    String FIELD_TYPE_PROP_NAME = "bronz.util.parser.fieldtype";
    String DEFAULT_VALUES_PROP_NAME = "bronz.util.parser.fielddefaultvalue";
    String EMPTY_STRING = "";
    String SEPARATOR = ",";
    
    
    List<T> getParsedObjectList();
    Map<U,T> getParsedObjectMap();
}
