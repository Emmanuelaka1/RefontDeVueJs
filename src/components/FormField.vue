<template>
  <div class="form-field" :class="{ empty: !label }" :data-testid="testId">
    <label v-if="label" class="field-label">{{ label }}</label>
    <span v-if="label" class="field-value">{{ modelValue }}</span>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    label?: string
    modelValue?: string
    testId?: string
  }>(),
  {
    label: '',
    modelValue: '',
    testId: 'form-field',
  }
)
</script>

<style scoped lang="scss">
.form-field {
  display: flex;
  flex-direction: column;
  gap: $space-xxs;
  min-height: 40px;

  &.empty {
    visibility: hidden;
  }
}

.field-label {
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  color: var(--text-label);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.field-value {
  background: var(--bg-input);
  font-size: $font-size-base;
  color: var(--text-primary);
  min-height: 24px;
  padding: $space-xxs $space-sm;
  border: 1px solid var(--border-light);
  border-radius: $border-radius-sm;
}
</style>
