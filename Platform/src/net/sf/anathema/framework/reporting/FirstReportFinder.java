package net.sf.anathema.framework.reporting;

import net.sf.anathema.framework.IApplicationModel;
import net.sf.anathema.framework.repository.IItem;

public class FirstReportFinder implements DefaultReportFinder {
  private IApplicationModel model;

  public FirstReportFinder(IApplicationModel model) {
    this.model = model;
  }

  @Override
  public Report getDefaultReport(IItem item) {
    Report[] reports = model.getReportRegistry().getReports(item);
    if (reports.length == 0) {
      return null;
    }
    return reports[0];
  }
}