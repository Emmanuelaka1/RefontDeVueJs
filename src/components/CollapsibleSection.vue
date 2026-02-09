<template>
  <div class="collapsible-section" :data-testid="`section-${sectionId}`">
    <!-- Header -->
    <div
      class="section-header"
      :data-testid="`section-header-${sectionId}`"
      @click="$emit('toggle')"
    >
      <i
        class="pi chevron"
        :class="expanded ? 'pi-chevron-down' : 'pi-chevron-right'"
      />
      <span class="header-line" />
      <h3 class="header-title">{{ title }}</h3>
      <span class="header-line" />
    </div>

    <!-- Body -->
    <Transition name="slide">
      <div
        v-show="expanded"
        class="section-body"
        :data-testid="`section-body-${sectionId}`"
      >
        <slot />
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  sectionId: string
  expanded: boolean
}>()

defineEmits<{
  toggle: []
}>()
</script>

<style scoped lang="scss">
.collapsible-section {
  background: $white;
  border: 1px solid $gray-300;
  border-radius: $border-radius;
  margin-bottom: $space-md;
}

.section-header {
  display: flex;
  align-items: center;
  gap: $space-sm;
  padding: $space-sm $space-lg;
  cursor: pointer;
  user-select: none;

  &:hover {
    .header-title {
      color: $primary;
    }
  }
}

.chevron {
  font-size: 12px;
  color: $primary;
  transition: transform $transition-base;
  flex-shrink: 0;
}

.header-line {
  flex: 1;
  height: 1px;
  background: $gray-300;
}

.header-title {
  font-size: $font-size-sm;
  font-weight: $font-weight-bold;
  color: $gray-800;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  white-space: nowrap;
  padding: 0 $space-xs;
}

.section-body {
  padding: $space-md $space-xl $space-lg;
}

// Transition
.slide-enter-active,
.slide-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}
.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
  padding-top: 0;
  padding-bottom: 0;
}
.slide-enter-to,
.slide-leave-from {
  max-height: 800px;
  opacity: 1;
}
</style>
