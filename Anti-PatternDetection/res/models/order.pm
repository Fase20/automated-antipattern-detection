dtmc

const double c1 = 1.2;
const double rt1 = 150;
const double p1 = 0.995;

const double c2 = 0.6;
const double rt2 = 200;
const double p2 = 0.95;

const double c3 = 0.6;
const double rt3 = 200;
const double p3 = 0.95;

const double timeout = 2000;

module ORDER

  s : [1..9] init 1;

  [] s=1 -> p1:(s'=4) + (1-p1):(s'=2);
  [] s=2 -> p2:(s'=5) + (1-p2):(s'=3);
  [] s=3 -> p3:(s'=6) + (1-p3):(s'=7);

  [] s=4 -> (s'=8);
  [] s=5 -> (s'=8);
  [] s=6 -> (s'=8);

  [] s=7 -> (s'=9); //failure
  [] s=8 -> (s'=9); //success
  [] s=9 -> (s'=9); //done
endmodule

label "success" = s=8;
label "done" = s=9;

rewards "cost"
  s=1 : c1+c2+c3;
endrewards

rewards "rt"
  s=4  : rt1;
  s=5  : rt2;
  s=6  : rt3;
  s=7  : timeout;
endrewards