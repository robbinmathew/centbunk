package bronz.accounting.bunk.util;

import bronz.accounting.bunk.model.ScanType;
import bronz.accounting.bunk.model.ScannedDetail;
import bronz.accounting.bunk.scanner.ScanDataParser;
import junit.framework.TestCase;
import org.apache.poi.util.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ScanDataParserTest extends TestCase {

    private ScanDataParser scanDataParser = new ScanDataParser();

    public void testParsePriceUpdate() throws Exception{
        String fileString = new String(IOUtils.toByteArray(ScanDataParserTest.class.getClassLoader().getResourceAsStream("scandata/price_html_data.xml")), StandardCharsets.UTF_8);
        List<ScannedDetail> items = scanDataParser.parse(ScanType.PRICE_CHANGE_TYPE, fileString, ScanDataParserTest.class.getSimpleName(), false);
        System.out.println(items);
    }

    public void testParseInvoiceUpdate() throws Exception{
        String fileString = new String(IOUtils.toByteArray(ScanDataParserTest.class.getClassLoader().getResourceAsStream("scandata/invoices_html_with_oil_prods.xml")), StandardCharsets.UTF_8);
        List<ScannedDetail> items = scanDataParser.parse(ScanType.PROD_RECEIPT, fileString, ScanDataParserTest.class.getSimpleName(), false);
        System.out.println(items);
    }
}
