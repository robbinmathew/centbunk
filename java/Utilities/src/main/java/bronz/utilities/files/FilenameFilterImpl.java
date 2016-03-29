package bronz.utilities.files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import bronz.utilities.general.ValidationUtil;

public class FilenameFilterImpl implements FilenameFilter
{
    private final List<String> fileNamesToBeFiltered;
    public FilenameFilterImpl( final String... fileNames )
    {
    	ValidationUtil.checkIfNull( fileNames, "file names array" );
        this.fileNamesToBeFiltered = Arrays.asList( fileNames );
    }
    public boolean accept( final File dir, final String name )
    {
        final boolean accept;
        if ( this.fileNamesToBeFiltered.contains( name ) )
        {
            accept = false;
        }
        else
        {
            accept = true;
        }
        return accept;
    }
}
