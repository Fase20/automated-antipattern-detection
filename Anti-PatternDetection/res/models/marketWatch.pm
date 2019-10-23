dtmc

const double x1 = 0.92;
const double c1 = 8;
const double rt1 = 20;
const double p1 = 0.993;

const double x2 = 0.003;
const double c2 = 6.5;
const double rt2 = 30;
const double p2 = 0.992;

const double timeout = 2000;

module MARKET_WATCH

  s : [1..8] init 1;

  [] s=1 -> x1:(s'=2) + x2:(s'=3);

  [] s=2 -> p1:(s'=4) + (1-p1):(s'=6);
  [] s=3 -> p2:(s'=5) + (1-p2):(s'=6);

  [] s=4 -> (s'=7);
  [] s=5 -> (s'=7);

  [] s=6 -> (s'=8); //failure
  [] s=7 -> (s'=8); //success
  [] s=8 -> (s'=8); //done
endmodule

label "success" = s=7;
label "done" = s=8;

rewards "cost"
  s=2 : c1;
  s=3 : c2;
endrewards

rewards "rt"
  s=4 : rt1;
  s=5 : rt2;
  s=6 : timeout;
endrewards