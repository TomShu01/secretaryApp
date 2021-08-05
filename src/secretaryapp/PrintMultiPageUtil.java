package secretaryapp;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;

import javax.swing.RepaintManager;

public class PrintMultiPageUtil implements Printable, Pageable {
    private Component componentToBePrinted;
    private PageFormat format;
    private int numPages;

    public PrintMultiPageUtil(Component componentToBePrinted) {
        this.componentToBePrinted = componentToBePrinted;

        // get total space from component  
        Dimension totalSpace = this.componentToBePrinted.getPreferredSize();

        // calculate for DIN A4
        format = PrinterJob.getPrinterJob().defaultPage();
        numPages = (int) Math.ceil(totalSpace .height/format.getImageableHeight());
    }

    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();

        // show page-dialog with default DIN A4
        format = printJob.pageDialog(printJob.defaultPage());

        printJob.setPrintable(this);
        printJob.setPageable(this);

        if (printJob.printDialog())
            try {
                printJob.print();
            } catch(PrinterException pe) {
                System.out.println("Error printing: " + pe);
            }
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if ((pageIndex < 0) | (pageIndex >= numPages)) {
            return(NO_SUCH_PAGE);
        } else {
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY() - pageIndex * pageFormat.getImageableHeight());
            disableDoubleBuffering(componentToBePrinted);
            componentToBePrinted.paint(g2d);
            enableDoubleBuffering(componentToBePrinted);
            return(PAGE_EXISTS);
        }
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }

    @Override
    public int getNumberOfPages() {
        // TODO Auto-generated method stub
        return numPages;
    }

    @Override
    public PageFormat getPageFormat(int arg0) throws IndexOutOfBoundsException {
        return format;
    }

    @Override
    public Printable getPrintable(int arg0) throws IndexOutOfBoundsException {
        // TODO Auto-generated method stub
        return this;
    }
}
