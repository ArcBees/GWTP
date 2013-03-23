package com.arcbees.carsample.client.application.ui;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;

public class ShowMorePagerPanel extends AbstractPager {
    private static final int DEFAULT_INCREMENT = 20;

    private int incrementSize = DEFAULT_INCREMENT;
    private int lastScrollPos = 0;

    private final ScrollPanel scrollable = new ScrollPanel();

    public ShowMorePagerPanel(int pageSize) {
        incrementSize = pageSize;
        initWidget(scrollable);

        // Do not let the scrollable take tab focus.
        scrollable.getElement().setTabIndex(-1);

        // Handle scroll events.
        scrollable.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                // If scrolling up, ignore the event.
                int oldScrollPos = lastScrollPos;
                lastScrollPos = scrollable.getVerticalScrollPosition();
                if (oldScrollPos >= lastScrollPos) {
                    return;
                }

                HasRows display = getDisplay();
                if (display == null) {
                    return;
                }

                int maxScrollTop = scrollable.getWidget().getOffsetHeight()
                        - scrollable.getOffsetHeight();
                if (lastScrollPos >= maxScrollTop - 16) {
                    //We are near the end, so increase the page size.
                    int newPageSize = Math.min(display.getVisibleRange()
                            .getLength() + incrementSize, display.getRowCount());
                    display.setVisibleRange(0, newPageSize);
                }
            }
        });
    }

    public int getIncrementSize() {
        return incrementSize;
    }

    public void setIncrementSize(int incrementSize) {
        this.incrementSize = incrementSize;
    }

    @Override
    public void setDisplay(HasRows display) {
        assert display instanceof Widget : "display must extend Widget";
        scrollable.setWidget((Widget) display);
        super.setDisplay(display);
    }

    @Override
    protected void onRangeOrRowCountChanged() {
    }
}
