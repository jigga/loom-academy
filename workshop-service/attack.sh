case  $1 in
  fibers)
    echo "GET http://localhost:8080/loom/test" | vegeta attack -duration=120s -timeout=300s -rate=400 | tee  $1.bin | vegeta report && cat $1.bin | vegeta plot > $1.html
      ;;
  threads)
    echo "GET http://localhost:8081/loom/test" | vegeta attack -duration=120s -timeout=300s -rate=100 | tee  $1.bin | vegeta report && cat $1.bin | vegeta plot > $1.html
      ;;
  *)
    echo "Unknown command. Usage: $0 [fibers|threads]"
esac

#echo "GET http://localhost:8080/workshop/test" | vegeta attack -duration=30s -timeout=30s -rate=100 | tee  $1.bin | vegeta report && cat $1.bin | vegeta plot > $1.html
