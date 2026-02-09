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
  color: $gray-600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.field-value {
  background: $white;
  font-size: $font-size-base;
  color: $gray-900;
  min-height: 24px;
  padding: $space-xxs $space-sm;
  border: 1px solid $gray-200;
  border-radius: $border-radius-sm;
}
</style>
