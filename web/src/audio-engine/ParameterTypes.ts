export type ParameterValue = number | string | boolean;

export type ParameterKind = 'number' | 'select' | 'boolean';

export interface ParameterOption {
  value: string;
  label: string;
}

export interface ParameterDefinition<T extends ParameterValue = ParameterValue> {
  id: string;
  label: string;
  kind: ParameterKind;
  value: T;
  min?: number;
  max?: number;
  step?: number;
  unit?: string;
  options?: ParameterOption[];
  automatable?: boolean;
}

export type ParameterMap = Record<string, ParameterDefinition>;
