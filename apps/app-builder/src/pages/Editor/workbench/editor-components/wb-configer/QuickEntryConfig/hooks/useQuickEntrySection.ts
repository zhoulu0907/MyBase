import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { usePageEditorSignal } from '@onebase/ui-kit';
import type { QuickEntryProps } from '../types';

type SectionKey = keyof QuickEntryProps;

interface UpdateOptions {
  replace?: boolean;
}

export function useQuickEntrySection<K extends SectionKey>(
  cpID: string,
  section: K,
  defaultValue: NonNullable<QuickEntryProps[K]>
) {
  const { curComponentSchema, setCurComponentSchema, setPageComponentSchemas } = usePageEditorSignal();
  const [state, setState] = useState<NonNullable<QuickEntryProps[K]>>(defaultValue);
  const defaultValueRef = useRef(defaultValue);

  useEffect(() => {
    defaultValueRef.current = defaultValue;
  }, [defaultValue]);

  const schemaSection = useMemo(
    () => (curComponentSchema?.config?.props?.[section] as QuickEntryProps[K]) || undefined,
    [curComponentSchema, section]
  );

  useEffect(() => {
    let nextValue: NonNullable<QuickEntryProps[K]>;
    if (schemaSection === undefined) {
      nextValue = defaultValueRef.current;
    } else if (typeof schemaSection === 'object' && schemaSection !== null) {
      nextValue = {
        ...(defaultValueRef.current as Record<string, unknown>),
        ...(schemaSection as Record<string, unknown>)
      } as NonNullable<QuickEntryProps[K]>;
    } else {
      nextValue = schemaSection as NonNullable<QuickEntryProps[K]>;
    }

    setState((prev) => {
      const isEqual =
        typeof prev === 'object' && prev !== null && typeof nextValue === 'object' && nextValue !== null
          ? shallowEqual(prev as Record<string, unknown>, nextValue as Record<string, unknown>)
          : Object.is(prev, nextValue);

      return isEqual ? prev : nextValue;
    });
  }, [schemaSection]);

  const commit = useCallback(
    (value: NonNullable<QuickEntryProps[K]>) => {
      if (!curComponentSchema?.config) return;
      const nextSchema = {
        ...curComponentSchema,
        config: {
          ...curComponentSchema.config,
          props: {
            ...curComponentSchema.config.props,
            [section]: value
          }
        }
      };
      setCurComponentSchema(nextSchema);
      setPageComponentSchemas(cpID, nextSchema);
    },
    [cpID, curComponentSchema, section, setCurComponentSchema, setPageComponentSchemas]
  );

  const update = useCallback(
    (patch: Partial<NonNullable<QuickEntryProps[K]>> | NonNullable<QuickEntryProps[K]>, options?: UpdateOptions) => {
      setState((prev) => {
        const nextValue =
          options?.replace || typeof patch !== 'object'
            ? (patch as NonNullable<QuickEntryProps[K]>)
            : ({
                ...(prev as Record<string, unknown>),
                ...(patch as Record<string, unknown>)
              } as NonNullable<QuickEntryProps[K]>);
        commit(nextValue);
        return nextValue;
      });
    },
    [commit]
  );

  return [state, update] as const;
}

function shallowEqual(a: Record<string, unknown>, b: Record<string, unknown>) {
  const aKeys = Object.keys(a);
  const bKeys = Object.keys(b);
  if (aKeys.length !== bKeys.length) {
    return false;
  }
  for (const key of aKeys) {
    if (!Object.prototype.hasOwnProperty.call(b, key) || a[key] !== b[key]) {
      return false;
    }
  }
  return true;
}
