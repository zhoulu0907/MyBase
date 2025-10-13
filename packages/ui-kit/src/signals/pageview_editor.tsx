import { PageView } from '@onebase/app';
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
        Object.entries(pageViews.value).map(([id, pv]) => [id, { ...pv, isDefaultDetailViewMode: 0 }])
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

  return {
    pageViews,
    addPageView,
    clearPageViews,
    updatePageView,
    setPageViews,

    curViewId,
    setCurViewId,
    clearCurViewId
  };
};

export const usePageViewEditorSignal = createPageViewEditorSignal();
