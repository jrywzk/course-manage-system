<template>
  <div class="app-logo" :class="{ 'logo-small': small, 'logo-landing': landing }">
    <svg
      :width="small ? 28 : landing ? 40 : 32"
      :height="small ? 28 : landing ? 40 : 32"
      viewBox="0 0 48 48"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <defs>
        <linearGradient id="logoGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#409EFF" />
          <stop offset="100%" stop-color="#67C23A" />
        </linearGradient>
        <linearGradient id="capGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#67C23A" />
          <stop offset="100%" stop-color="#409EFF" />
        </linearGradient>
        <filter id="glow">
          <feGaussianBlur stdDeviation="1.5" result="blur" />
          <feMerge>
            <feMergeNode in="blur" />
            <feMergeNode in="SourceGraphic" />
          </feMerge>
        </filter>
      </defs>

      <!-- 底部的打开书本（更饱满） -->
      <path
        d="M6 37V11C6 9.89543 6.89543 9 8 9H24V37H8C6.89543 37 6 36.1046 6 37Z"
        fill="url(#logoGrad)"
        opacity="0.65"
      />
      <path
        d="M42 37V11C42 9.89543 41.1046 9 40 9H24V37H40C41.1046 37 42 36.1046 42 37Z"
        fill="url(#logoGrad)"
        opacity="0.85"
      />

      <!-- 书脊 -->
      <rect x="22" y="9" width="4" height="28" rx="1.2" fill="url(#logoGrad)" />

      <!-- 左页文字线 -->
      <line x1="11" y1="17" x2="20" y2="17" stroke="#fff" stroke-width="1.5" stroke-linecap="round" opacity="0.55" />
      <line x1="11" y1="22" x2="20" y2="22" stroke="#fff" stroke-width="1.5" stroke-linecap="round" opacity="0.55" />
      <line x1="11" y1="27" x2="17" y2="27" stroke="#fff" stroke-width="1.5" stroke-linecap="round" opacity="0.55" />

      <!-- 右页文字线 -->
      <line x1="28" y1="17" x2="37" y2="17" stroke="#fff" stroke-width="1.5" stroke-linecap="round" opacity="0.55" />
      <line x1="28" y1="22" x2="37" y2="22" stroke="#fff" stroke-width="1.5" stroke-linecap="round" opacity="0.55" />
      <line x1="28" y1="27" x2="31" y2="27" stroke="#fff" stroke-width="1.5" stroke-linecap="round" opacity="0.55" />

      <!-- 帽穗 -->
      <path
        d="M24 18L24 10"
        stroke="url(#logoGrad)"
        stroke-width="2"
        stroke-linecap="round"
      />
      <circle cx="24" cy="9" r="3.5" fill="url(#capGrad)" filter="url(#glow)" />

      <!-- 帽子平面 -->
      <path
        d="M8 18L24 12L40 18L24 24L8 18Z"
        fill="url(#logoGrad)"
        opacity="0.9"
      />
      <!-- 帽沿 -->
      <rect x="6" y="20" width="36" height="3" rx="1.5" fill="url(#capGrad)" opacity="0.85" />
    </svg>
    <span class="logo-text" :class="{ 'text-small': small, 'text-landing': landing }">
      {{ landing ? '学生选课管理系统' : '选课系统' }}
    </span>
  </div>
</template>

<script setup>
defineProps({
  small: { type: Boolean, default: false },
  landing: { type: Boolean, default: false }
})
</script>

<style scoped>
.app-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  user-select: none;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(45deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2px;
  white-space: nowrap;
}

.text-small {
  font-size: 16px;
  letter-spacing: 1px;
}

.logo-landing {
  justify-content: center;
  gap: 14px;
}

.text-landing {
  font-size: 24px;
  letter-spacing: 3px;
}
</style>
