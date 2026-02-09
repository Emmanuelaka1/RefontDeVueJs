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
  min-height: 28px;

  &.empty {
    visibility: hidden;
  }
}

.field-label {
  font-size: $font-size-base;
  font-weight: $font-weight-normal;
  color: $gray-700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.field-value {
  background-color: $gray-200;
  font-size: $font-size-base;
  color: $gray-900;
  min-height: 20px;
}
</style>
