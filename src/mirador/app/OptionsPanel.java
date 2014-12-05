/* COPYRIGHT (C) 2014 Fathom Information Design. All Rights Reserved. */

package mirador.app;

import java.util.ArrayList;

import mirador.ui.Interface;
import mirador.ui.SoftFloat;
import mirador.ui.Widget;
import processing.core.PFont;

/**
 * Panel with main options in Mirador (load data, plot type, P-Value selection, 
 * etc.)
 *
 */

public class OptionsPanel extends MiraWidget {
  int bColor;
  PFont h1Font;
  int h1Color;
  PFont pFont;
  int pColor;    
  int corColor;
  int misColor;
  MenuButton loadBtn, exportBtn, pdfBtn;
//  MenuButton uploadBtn;
  Options plotOpt, statOpt, mdatOpt;

  public OptionsPanel(Interface intf, float x, float y, float w, float h) {
    super(intf, x, y, w, h);
  }
  
  public void setup() {
    bColor = getStyleColor("OptionsPanel", "background");
    h1Font = getStyleFont("OptionsPanel.h1", "font-family", "font-size");
    h1Color = getStyleColor("OptionsPanel.h1", "color");

    pFont = getStyleFont("OptionsPanel.p", "font-family", "font-size");
    pColor = getStyleColor("OptionsPanel.p", "color");
    
    corColor = getStyleColor("RowPlots.Pvalue", "background-color");
    misColor = getStyleColor("RowPlots.MissingData", "background-color");
    
    loadBtn = new MenuButton(intf, 10, 60, 100, 25, "Load Data") {
      public void handle() {
        if (keyPressed(SHIFT)) {
          mira.reloadDataset();
        } else {
          mira.loadDataset();  
        }        
      }
    };
    loadBtn.setShiftLabel("Reload");
    addChild(loadBtn, TOP_LEFT_CORNER);
    
    exportBtn = new MenuButton(intf, 10, 90, 100, 25, "Export selection") {
      public void handle() {
        mira.exportSelection();
      }
    };
    addChild(exportBtn, TOP_LEFT_CORNER);

//    uploadBtn = new MenuButton(intf, 10, 120, 100, 25, "Upload Findings") {
//      public void handle() {
//        mira.uploadSession();
//      }
//    };
//    addChild(uploadBtn, TOP_LEFT_CORNER);
    
//    pdfBtn = new MenuButton(intf, 10, 150, 100, 25, "Save PDF") {
//      public void handle() {
//        mira.savePDF();
//      }
//    };
//    addChild(pdfBtn, TOP_LEFT_CORNER);
    
    pdfBtn = new MenuButton(intf, 10, 120, 100, 25, "Save PDF") {
      public void handle() {
        mira.savePDF();
      }
    };
    addChild(pdfBtn, TOP_LEFT_CORNER); 
    
    plotOpt = new Options(10, 165, 110, 80, "PlotOptions");
    plotOpt.title("Plot Type");
    plotOpt.add("Scatter", "Histogram", "Eikosogram");
    plotOpt.select(mira.getPlotType());
    
    statOpt = new Options(10, 295, 110, 80, "StatsOptions");
    statOpt.title("P-value", corColor);
    statOpt.add("99.9%", "99.5%", "99%", "95%", "90%", "Don't use");
    statOpt.select(mira.getPValue());    
    
    mdatOpt = new Options(10, 495, 110, 80, "SessionOptions");
    mdatOpt.title("Available\ndata", misColor);
    mdatOpt.add("90%", "80%", "40%", "20%", "Don't use");
    mdatOpt.select(mira.getMissingThreshold());    
  }
  
  public void update() {
    plotOpt.update();
    statOpt.update();
    mdatOpt.update();    
  }
  
  public void draw() {
    noStroke();
    fill(bColor);
    rect(0, 0, width, height);

    fill(h1Color);
    textFont(h1Font);
    text(MiraApp.APP_NAME, 10, 25); 
    
    fill(color(0), 150);
    textFont(pFont);
    text(MiraApp.APP_VERSION, 10, 45); 
    
    plotOpt.draw();
    statOpt.draw();
    mdatOpt.draw();
  }
  
  public void postDraw() { 
    // shadows
    beginShape(QUAD);
    fill(color(0), 50);
    vertex(width, 0);
    vertex(width, height);
    fill(color(0), 0);
    vertex(width - 15, height);
    vertex(width - 15, 0);      
    endShape();
  }
  
  public void mousePressed() {
    if (plotOpt.select(mouseX, mouseY)) {
      mira.setPlotType(plotOpt.selected);
    } else if (statOpt.select(mouseX, mouseY)) {
      mira.setPValue(statOpt.selected);
    } else if (mdatOpt.select(mouseX, mouseY)) {
      mira.setMissingThreshold(mdatOpt.selected);
    }
  }  
  
  protected void handleResize(int newWidth, int newHeight) {
    bounds.h.set(newHeight);
  }  
  
  class Options {
    ArrayList<String> list;
    int selected;
    int x, y, w, h;
    String[] title;
    boolean showTab;
    int tColor;
    float bWidth;
    int bColor;
    PFont h1Font;
    int h1Color;
    float sWidth, sHeight, sRight;
    int sColor;    
    float selY0;
    SoftFloat selY;
    
    Options(int x, int y, int w, int h, String style) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      
      h1Font = getStyleFont("OptionsPanel." + style + ".h1", 
                            "font-family", "font-size");
      h1Color = getStyleColor("OptionsPanel." + style + ".h1", "color");      
      bWidth = getStyleSize("OptionsPanel." + style, "border-top-width");
      bColor = getStyleColor("OptionsPanel." + style, "border-top-color");      
      
      sWidth = getStyleSize("OptionsPanel." + style + ".SelectBox", "width");
      sHeight = getStyleSize("OptionsPanel." + style + ".SelectBox", "height");
      sRight = getStyleSize("OptionsPanel." + style + ".SelectBox", "right");
      sColor = getStyleColor("OptionsPanel." + style + ".SelectBox", "background");
      
      list = new ArrayList<String>();
      
      selY = new SoftFloat();
    }

    void title(String titl) {
      title = titl.split("\n");
      showTab = false;
    }    
    
    void title(String titl, int col) {
      title = titl.split("\n");
      tColor = col;
      showTab = true;
    }
    
    void add(String... options) {
      for (String opt: options) {
        list.add(opt);
      }
    }
    
    void select(int sel) {
      selected = sel;
      selY.set(selected * sHeight);
    }
    
    boolean select(int mx, int my) {
      float x0 = x + 5;
      float x1 = x0 + sWidth;
      float y0 = selY0;
      float y1 = selY0 + list.size() * sHeight;
      
      if (x0 <= mx && mx <= x1 && y0 <= my && my <= y1) {
        int sel = (int)((my - y0) / sHeight);
        if (sel != selected) {
          selected = sel;
          selY.setTarget(selected * sHeight);
          return true; 
        }
      }
      return false;
    }
    
    void update() {
      selY.update();
    }
    
    void draw() {
      stroke(bColor);
      strokeWeight(bWidth);
      line(x, y, x + w - 10, y);
      
      float x1 = x + 5;
      float y0 = y + 10;
      float y1 = y0;
      
      fill(h1Color);
      textFont(h1Font);
      float th = (textAscent() + textDescent()) * 1.1f;

      for (String t: title) {
        y1 += th;
        text(t, x1, y1);        
      }
      
      if (showTab) {
        noStroke();
        fill(tColor);
        y0 += th - textAscent();
        rect(0, y0, 5, y1 - y0);
      }

      y1 += 10;
      selY0 = y1;
      float y2 = y1 + selY.get();
      noStroke();
      fill(sColor);
      rect(x, y2, sWidth, sHeight);
      
      fill(pColor);
      textFont(pFont);

      float center = (sHeight - pFont.getSize()) / 2;
      for (String opt: list) {
        y1 += sHeight;
        text(opt, x1, y1 - center);        
      }
    }
  }
  
  protected class MenuButton extends Widget {
    String label;
    String shiftLabel;
    int color;    
    SoftFloat hoverAlpha;
    
    public MenuButton(Interface intf, float x, float y, float w, float h, 
                      String label) {
      super(intf, x, y, w, h);
      this.label = label;
      this.shiftLabel = null;
      color = getStyleColor("OptionsPanel.Button", "color");
      hoverAlpha = new SoftFloat(90);
    }
    
    public void setShiftLabel(String label) {
      shiftLabel = label;
    }
    
    public void update() {
      hoverAlpha.update();
    }
    
    public void draw() {
      fill(color, hoverAlpha.getFloor());
      rect(0, 0, width, height);
      
      float center = (height - pFont.getSize()) / 2;
      
      fill(pColor);
      textFont(pFont);
      if (shiftLabel != null && hovered && keyPressed(SHIFT)) {
        text(shiftLabel, 5, height - center);
      } else {
        text(label, 5, height - center);
      }
            
    }  
    
    public void hoverIn() {
      hoverAlpha.set(0);
      hoverAlpha.setTarget(255);
    }    

    public void hoverOut() {
      hoverAlpha.set(90);
    }
  }
}
