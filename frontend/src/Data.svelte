<script context="module">
    export function sanitizeYear(year) {
      const index = year.indexOf('–')
      if (index != -1) {
        return year.substr(0, index)   
      }
      return year
    }
</script>