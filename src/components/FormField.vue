<template>
  <div class="form-field" :class="{ empty: !label }" :data-testid="testId">
    <label v-if="label" class="field-label">{{ label }}</label>
    <InputText
      v-if="label"
      :modelValue="modelValue"
      :readonly="readonly"
      class="field-input"
      @update:modelValue="$emit('update:modelValue', $event ?? '')"
    />
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    label?: string
    modelValue?: string
    readonly?: boolean
    testId?: string
  }>(),
  {
    label: '',
    modelValue: '',
    readonly: true,
    testId: 'form-field',
  }
)

defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<style scoped lang="scss">
.form-field {
  display: flex;
  flex-direction: column;
  gap: $space-xxs;
  min-height: 38px;

  &.empty {
    visibility: hidden;
  }
}

.field-label {
  font-size: $font-size-xs;
  font-weight: $font-weight-medium;
  color: $gray-500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.field-input {
  font-size: $font-size-sm !important;
  padding: 4px $space-sm !important;
  border-radius: $border-radius-sm !important;
  border: 1px solid $gray-300 !important;
  background: $gray-50 !important;
  height: 26px !important;
  width: 100%;
  box-shadow: $shadow-inset;

  &:read-only {
    background: $gray-50 !important;
    color: $gray-600;
    cursor: default;
    border-color: $border-color-light !important;
    box-shadow: none;
  }
}
</style>
