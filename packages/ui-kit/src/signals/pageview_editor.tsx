import { PageView, ViewType } from '@onebase/app';
import { signal } from '@preact/signals-react';

export const createPageViewEditorSignal = () => {
  const pageViews = signal<{ [key: string]: PageView }>({});
  const curViewId = signal<string>('');

  const setPageViews = (initPageViews: PageView[]) => {
    pageViews.value = initPageViews.reduce(
      (acc, pv) => {
        acc[pv.id] = pv;
        return acc;
      },
      {} as { [key: string]: PageView }
    );
  };

  const addPageView = (pageView: PageView) => {
    pageViews.value = { ...pageViews.value, [pageView.id]: pageView };
  };

  const clearPageViews = () => {
    pageViews.value = {};
  };

  const updatePageViewName = (id: string, name: string) => {
    pageViews.value[id].pageName = name;
  };

  const updatePageView = (pageView: PageView) => {
    if (pageView.isDefaultDetailViewMode) {
      const updatedPageViews = Object.fromEntries(
        Object.entries(pageViews.value).map(([id, pv]) => [id, { ...pv, isDefaultDetailViewMode: 0 }])
      );
      pageViews.value = {
        ...updatedPageViews,
        [pageView.id]: pageView
      };
    }

    if (pageView.isDefaultEditViewMode) {
      const updatedPageViews = Object.fromEntries(
        Object.entries(pageViews.value).map(([id, pv]) => [id, { ...pv, isDefaultEditViewMode: 0 }])
      );
      pageViews.value = {
        ...updatedPageViews,
        [pageView.id]: pageView
      };
    }

    pageViews.value = { ...pageViews.value, [pageView.id]: pageView };
  };

  const setCurViewId = (id: string) => {
    curViewId.value = id;
  };

  const clearCurViewId = () => {
    curViewId.value = '';
  };

  const getDefaultEditPageViewId = () => {
    return Object.values(pageViews.value).find((pv) => pv.isDefaultEditViewMode == 1)?.id;
  };

  const getDefaultDetailPageViewId = () => {
    return Object.values(pageViews.value).find((pv) => pv.isDefaultDetailViewMode == 1)?.id;
  };

  const getPageViewType = (id: string) => {
    const pageView = pageViews.value[id];
    if (pageView.editViewMode == 1 && pageView.detailViewMode == 1) {
      return ViewType.MIX;
    } else if (pageView.editViewMode == 1) {
      return ViewType.EDIT;
    } else if (pageView.detailViewMode == 1) {
      return ViewType.DETAIL;
    }
    return ViewType.UNKNOWN;
  };

  return {
    pageViews,
    addPageView,
    clearPageViews,
    updatePageView,
    updatePageViewName,
    setPageViews,

    getDefaultEditPageViewId,
    getDefaultDetailPageViewId,
    getPageViewType,

    curViewId,
    setCurViewId,
    clearCurViewId
  };
};

export const usePageViewEditorSignal = createPageViewEditorSignal();
