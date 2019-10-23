#!/bin/sh
#cd "$(dirname "$0")"
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
/usr/local/bin/storm -pc --prism "$DIR/../models/fundamentalAnalysis.pm" --prop "P=?[F\"success\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states)/Psuccess/g'
/usr/local/bin/storm -pc --prism "$DIR/../models/fundamentalAnalysis.pm" --prop "R{\"cost\"}=?[F\"done\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states)/Cost/g'
/usr/local/bin/storm -pc --prism "$DIR/../models/fundamentalAnalysis.pm" --prop "R{\"rt\"}=?[F\"done\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states)/Response time/g'

echo "Fundamental Analysis Properties" > "$DIR/../output.txt"
/usr/local/bin/storm -pc --prism "$DIR/../models/fundamentalAnalysis.pm" --prop "P=?[F\"success\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states)/Psuccess/g' >> "$DIR/../output.txt"
/usr/local/bin/storm -pc --prism "$DIR/../models/fundamentalAnalysis.pm" --prop "R{\"cost\"}=?[F\"done\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states)/Cost/g' >> "$DIR/../output.txt"
/usr/local/bin/storm -pc --prism "$DIR/../models/fundamentalAnalysis.pm" --prop "R{\"rt\"}=?[F\"done\"]" | grep "Result (for initial states)" | sed 's/Result (for initial states)/Response time/g' >> "$DIR/../output.txt"
exit