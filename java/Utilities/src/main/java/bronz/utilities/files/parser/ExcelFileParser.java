package bronz.utilities.files.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelFileParser<T, U> extends AbstractFileParser<T, U>
{
    protected String sheetName;
    private FileInputStream inputStream;
    public ExcelFileParser( final Properties properties )
    {
        super( properties );
        this.sheetName = properties.getProperty( EXCEL_SHEETNAME_PROP_NAME,
                EMPTY_STRING );
    }
    
    public ExcelFileParser( final String filePath, final String className,
            final String keyField, final String sheetName,
            final String fieldNames, final String fieldTypes,
            final String fieldDefaultValues )
    {
        super( filePath, className, keyField, fieldNames, fieldTypes,
                fieldDefaultValues );
        this.sheetName = sheetName;
    }

    public List<T> getParsedObjectList()
    {
        final List<T> list = new LinkedList<T>();
        parseFile( list, null );
        return list;
    }

    public Map<U, T> getParsedObjectMap()
    {
        final Map<U, T> map = new HashMap<U, T>();
        parseFile( null, map );
        return map;
    }
    
    private HSSFSheet open() throws IOException
    {
        final File file =  new File( this.filePath );
        this.inputStream = new FileInputStream( file );
        final POIFSFileSystem fileSystem = new POIFSFileSystem(
                this.inputStream );
        final HSSFWorkbook workBook = new HSSFWorkbook( fileSystem );
        return workBook.getSheet( this.sheetName );
    }
    
    private void close()
    {
        try
        {
            if ( null != this.inputStream )
            {
                this.inputStream.close();
            }
        }
        catch( Exception exception )
        {
            throw new ParserException( exception );
        }
    }
    
    private void parseFile( final List<T> list, final Map<U, T> map )
    {
        // Current logic ignores the empty cells.
        try
        {
            final HSSFSheet sheet = open();
            HSSFRow row;
            HSSFCell cell;
            
            int rows = sheet.getPhysicalNumberOfRows();

            final int cols = this.propertiesMetadata.size();
            
            for( int r = 0; r < rows; r++ )
            {
                row = sheet.getRow( r );
                if( row != null )
                {
                    for( int c = 0; c < cols; c++ )
                    {
                        cell = row.getCell( c );
                        if( cell != null )
                        {
                            System.out.println( cell.getCellType() );// Your code here
                        }
                    }
                }
            }

            close();
        }
        catch( Exception exception )
        {
            throw new ParserException( exception );
        }

            
    }
}
