package bronz.accounting.bunk.scanner;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.framework.exceptions.BunkValidationException;
import bronz.accounting.bunk.model.ScanType;
import bronz.accounting.bunk.model.ScannedDetail;
import bronz.accounting.bunk.model.StockReceipt;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ScanDataParser {
    private static final Logger LOG = LogManager.getLogger( ScanDataParser.class );
    private static final ObjectMapper JSON_SERIALIZER = new ObjectMapper();

    private static List<String> KNOWN_PRICE_CHANGE_PRODS = Arrays.asList("HSD","Power","MS");
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"; //28/01/2021 06:00:00
    private static final String DATE_FORMAT1 = "dd/MM/yy"; //28/01/2021

    public List<ScannedDetail> parse(ScanType type, String data, String source, boolean includeRawContents) throws BunkMgmtException {
        try {
            if (type == ScanType.PRICE_CHANGE_TYPE)
                return parsePriceUpdateHtml(data, source, includeRawContents);
            else if (type == ScanType.PROD_RECEIPT)
                return parseInvoiceHtml(data, source, includeRawContents);
            else
                throw new IllegalStateException("Unhandled scantype:" + type);
        } catch (Exception e) {
            throw new BunkValidationException("Failed to parse type:" + type + " data:" + data, e);
        }
    }
    private List<ScannedDetail> parsePriceUpdateHtml(String priceUpdateHtml, String source, boolean includeRawContents) throws Exception {
        try {
            List<ScannedDetail> details = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            String cleanString = cleanString(priceUpdateHtml);
            Document document = builder.parse(new InputSource(new StringReader(cleanString)));
            Element root = document.getDocumentElement();
            if (!"table".equals(root.getTagName())) {
                throw new IllegalStateException("Unknown content:" + cleanString);
            }
            NodeList childElements = root.getChildNodes();
            for (int j=0; j < childElements.getLength(); j++) {
                Node child = childElements.item(j);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                NodeList rowList = child.getChildNodes();
                for (int i = 0; i < rowList.getLength(); i++) {
                    Node rowNode = rowList.item(i);
                    if (child.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    NodeList colList = rowNode.getChildNodes();
                    if (colList.getLength() <= 0 || colList.item(0).getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    if (colList.getLength() != 9) {
                        throw new IllegalStateException("Unexpected count of columns. The col positions may have changed. Please check. ColCount=" + colList.getLength() + " Expected=" + 9 + " Content:" + getNodeText(rowNode));
                    }

                    String prodName = colList.item(0).getTextContent();
                    if (!KNOWN_PRICE_CHANGE_PRODS.contains(prodName)) {
                        if (StringUtils.isNotBlank(prodName) &&
                                !"Product".equalsIgnoreCase(prodName)) {
                            throw new IllegalStateException("Unexpected row with prod name:" + prodName + " row:" + getNodeText(rowNode));
                        } else {
                            continue;
                        }
                    }

                    String newPrice = colList.item(1).getTextContent();
                    String effectiveDate = colList.item(2).getTextContent();
                    String productId = getProductId(colList.item(0).getTextContent());

                    int date = DateUtil.getIntegerEquivalent(parseDate(effectiveDate, DATE_FORMAT));

                    ScannedDetail scannedDetail = new ScannedDetail();
                    scannedDetail.setType(ScanType.PRICE_CHANGE_TYPE.getType());
                    scannedDetail.setIdentifier(productId);
                    scannedDetail.setDate(date);
                    scannedDetail.setComments(source);
                    if (includeRawContents) {
                        scannedDetail.setRawContents(getNodeText(rowNode));
                    }
                    scannedDetail.setContents(String.format("{\"productId\":%s, \"newPrice\":%s}",
                            productId, newPrice));

                    details.add(scannedDetail);
                }
            }
            return details;
        } catch (Exception e) {
            throw new BunkMgmtException("Failed to parse:" + priceUpdateHtml, e);
        }
    }

    private List<ScannedDetail> parseInvoiceHtml(String invoiceHtml, String source, boolean includeRawContents) throws Exception {
        List<ScannedDetail> details = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String cleanString = cleanString(invoiceHtml);
        Document document = builder.parse(new InputSource(new StringReader(cleanString)));
        Element root = document.getDocumentElement();
        if (!"table".equals(root.getTagName())) {
            throw new IllegalStateException("Unknown content:" + cleanString);
        }
        NodeList rowList = root.getChildNodes().item(0).getChildNodes();
        for (int i=0; i < rowList.getLength(); i++) {
            Node rowNode = rowList.item(i);
            NodeList colList = rowNode.getChildNodes();
            if (colList.getLength() <= 0 || getNodeText(rowNode).contains("<tr bgcolor=\"#e6e6e6\"><td><b>Total</b></td><td/><td/><td/><td/><td/><td/><td/><td/><td/><td/><td/><td/>"))
                continue;

            if (colList.getLength() != 17) {
                throw new IllegalStateException("Unexpected count of columns. The col positions may have changed. Please check. ColCount=" + colList.getLength() + " Expected=" + 17 + " Content:" + getNodeText(rowNode));

            }

            String supplyLocation = colList.item(2).getTextContent();

            String receiptType = null;
            if (StringUtils.isNotBlank(supplyLocation)) {
                if (supplyLocation.toLowerCase().startsWith("Irumpanam Terminal".toLowerCase())) {
                    receiptType = ScanType.PROD_RECEIPT.getType() + StockReceipt.FUEL_RECEIPT_TYPE;
                } else if (supplyLocation.toLowerCase().startsWith("COCHIN TML DUTY PAID".toLowerCase())) {
                    receiptType = ScanType.PROD_RECEIPT.getType() + StockReceipt.OIL_RECEIPT_TYPE;
                } else {
                    throw new IllegalStateException("Unexpected row with supplyLocation:" + supplyLocation + " row:" + getNodeText(rowNode));
                }
            } else {
                continue;
            }

            /**
             * <td>27</td>
             * <td align="left">20026664  /RI/11845</td>
             * <td align="left">Irumpanam Terminal                      </td>
             * <td>28/09/20</td>
             * <td align="left">2812000                  </td>
             * <td align="left">HSD - BS VI                   </td>
             * <td align="left">-</td>
             * <td align="right">8,000.000</td>
             * <td align="left">LT</td>
             * <td align="right">8.000</td>
             * <td align="left">KL</td>
             * <td align="right">72,965.8979</td>
             * <td align="left">KL</td>
             * <td align="right">583,727.18</td>
             * <td>20026004  /S4/11845/2.0</td>
             *
             * <td>-</td>
             *
             * <td align="right">KL64E1865    </td>
             */

            String invoicenumText = colList.item(1).getTextContent().trim().replaceAll("\\s+", "");
            String invoiceNum = invoicenumText.substring(0, invoicenumText.indexOf("/"));
            String dateText = colList.item(3).getTextContent();
            String productNameText = getProductName(colList);
            String quantityText = colList.item(7).getTextContent().trim();
            String totalPrice = colList.item(13).getTextContent();

            int date = DateUtil.getIntegerEquivalent(parseDate(dateText, DATE_FORMAT1));

            ScannedDetail scannedDetail = new ScannedDetail();
            scannedDetail.setType(receiptType);
            scannedDetail.setIdentifier(productNameText + "-" + invoiceNum);
            scannedDetail.setDate(date);
            scannedDetail.setComments(source);
            if (includeRawContents) {
                scannedDetail.setRawContents(getNodeText(rowNode));
            }

            ProdReceiptScanModel prodReceiptScanModel = new ProdReceiptScanModel();
            prodReceiptScanModel.setInvoiceNum(invoiceNum);
            prodReceiptScanModel.setProductNameText(productNameText);
            prodReceiptScanModel.setQuantity(new CustomDecimal(NumberFormat.getInstance().parse(quantityText).doubleValue()));
            prodReceiptScanModel.setTotalPrice(new CustomDecimal(NumberFormat.getInstance().parse(totalPrice).doubleValue()));

            scannedDetail.setContents(JSON_SERIALIZER.writeValueAsString(prodReceiptScanModel));

            details.add(scannedDetail);
        }
        return details;
    }

    private String getProductName(NodeList colList) {
        String prodType = colList.item(6).getTextContent().trim();
        if (prodType.equals("-")) {
            String item = colList.item(5).getTextContent().trim().replaceAll(" -BS VI", "").replaceAll(" - BS VI", "").replaceAll("\\s+", " ");
            if (item.startsWith("HSD"))
                return "HSD";
            else if (item.startsWith("MS") || item.startsWith("GASOHOL"))
                return "MS";
            else {
                throw new IllegalStateException("Unknown productname:" + item);
            }
        } else {
            return prodType;
        }
    }

    private String getProductId(String value) {
        if ("HSD".equalsIgnoreCase(value)) {
            return "2";
        } else if ("MS".equalsIgnoreCase(value)) {
            return "1";
        } else if ("Power".equalsIgnoreCase(value)) {
            return "3";
        } else {
            throw new IllegalStateException("Unknown product:" + value);
        }
    }



    private String cleanString(String originalString) {
        String result = originalString.replaceAll("&nbsp;", "")
                .replaceAll("\n", " ")
                .replaceAll("<colgroup>.*</colgroup>", "")
                .replaceAll(">\\s+<", "><");
        System.out.println(result);
        return result;
    }

    private String getNodeText(Node node) throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    private Date parseDate(String dateText, String format) throws ParseException {
        Date date = new SimpleDateFormat(format).parse(dateText);
        //System.out.println(dateText + "    " +  date.getTime());
        return date;
    }
}
