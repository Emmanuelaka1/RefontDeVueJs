<template>
  <div class="form-field" :class="{ empty: !label }" :data-testid="testId">
    <label v-if="label" class="field-label">{{ label }}</label>
    <span v-if="label" class="field-value" :class="{ 'field-value--empty': !modelValue }">
      {{ modelValue || '—' }}
    </span>
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
  align-items: center;
  gap: $space-sm;
  min-height: 34px;

  &.empty {
    visibility: hidden;
  }
}

.field-label {
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  color: var(--text-label);
  white-space: nowrap;
  width: 185px;
  flex-shrink: 0;
  line-height: 1.3;
}

.field-value {
  flex: 1;
  background: var(--bg-input);
  font-size: $font-size-sm;
  color: var(--text-primary);
  min-height: 28px;
  padding: $space-xs $space-md;
  border: 1px solid var(--border-input);
  border-radius: $border-radius;
  display: flex;
  align-items: center;
  transition: border-color $transition-base;

  &--empty {
    color: var(--text-muted);
    font-style: italic;
  }
}
</style>
