<template>
  <div class="collapsible-section" :data-testid="`section-${sectionId}`">
    <!-- Header -->
    <div
      class="section-header"
      :data-testid="`section-header-${sectionId}`"
      @click="$emit('toggle')"
    >
      <div class="chevron-wrapper">
        <img
          class="chevron"
          :src="expanded ? collapseIcon : expandIcon"
          alt=""
        />
      </div>
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
import collapseIcon from '@/assets/icons/collapse.svg'
import expandIcon from '@/assets/icons/expand.svg'

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
  margin-bottom: $space-sm;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0;
  padding: $space-sm $space-lg;
  cursor: pointer;
  user-select: none;
  transition: background $transition-fast;

  &:hover {
    .header-title {
      color: $primary;
    }

    .chevron-wrapper {
      background: $gray-300;
    }
  }
}

.chevron-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 25px;
  height: 25px;
  background: $gray-200;
  border-radius: $border-radius-sm;
  flex-shrink: 0;
  transition: background $transition-fast;
}

.chevron {
  width: 12px;
  height: 12px;
}

.header-line {
  height: 3px;
  background: $gray-900;

  &:first-of-type {
    width: 50px;
    flex-shrink: 0;
  }

  &:last-of-type {
    flex: 1;
  }
}

.header-title {
  font-size: $font-size-sm;
  font-weight: $font-weight-bold;
  color: $gray-800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  white-space: nowrap;
  padding: 0 $space-sm;
  transition: color $transition-fast;
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
